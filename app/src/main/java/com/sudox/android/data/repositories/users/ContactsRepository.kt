package com.sudox.android.data.repositories.users

import android.content.Context
import com.sudox.android.common.helpers.*
import com.sudox.android.data.database.dao.user.UserDao
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.exceptions.RequestRegexException
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.models.contacts.dto.*
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.NetworkException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                             private val authRepository: AuthRepository,
                                             private val accountRepository: AccountRepository,
                                             private val usersRepository: UsersRepository,
                                             private val userDao: UserDao,
                                             private val context: Context) {

    val contactsChannel: ConflatedBroadcastChannel<ArrayList<User>> = ConflatedBroadcastChannel()

    init {
        listenSessionState()
        listenContactsUpdates()
    }

    companion object {
        const val CONTACTS_NAME_REGEX_ERROR = 0
        const val CONTACTS_PHONE_REGEX_ERROR = 1
    }

    /**
     * Начинает прослушку состояния сессии.
     *
     * Если сессия завершена, то чистит канал контактов.
     * Если сессия установлена и канал контактов инициализирован - обновляет контакты.
     */
    private fun listenSessionState() = GlobalScope.launch(Dispatchers.IO) {
        for (state in authRepository.accountSessionStateChannel.openSubscription()) {
            if (!state) {
                contactsChannel.clear()
            } else if (contactsChannel.valueOrNull != null) {
                loadContacts()
            }
        }
    }

    /**
     * Начинает прослушку событий обновления контактов.
     *
     * 1) Добавление нового контакта
     * 2) Удаление контакта
     * 3) Синхронизация контактов
     * 4) Редактирование контакта
     */
    private fun listenContactsUpdates() {
        // Добавление контактов.
        protocolClient.listenMessage<ContactAddDTO>("updates.contacts.new") {
            saveNotifyContact(it)
        }

        // Удаление контактов.
        protocolClient.listenMessage<ContactRemoveDTO>("updates.contacts.remove") {
            removeNotifyContact(it)
        }

        // Синхронизация контактов
        protocolClient.listenMessage<ContactsIdsListDTO>("updates.contacts.sync") {
            syncNotifyContacts(it)
        }

        // Редактирование контакта
        protocolClient.listenMessage<ContactEditDTO>("updates.contacts.edit") {
            editNotifyContact(it)
        }
    }

    /**
     * Обновляет пользователя, когда приходит уведомление об обновлении контакта.
     */
    fun editNotifyContact(contactEditDTO: ContactEditDTO) = GlobalScope.async(Dispatchers.IO) {
        val user = usersRepository.loadActualUser(contactEditDTO.id).await()
                ?: return@async

        addContactMark(user)
        notifyContactUpdated(user)
    }

    /**
     * Сохраняет пользователя и ставит ему метку что он - контакт, когда приходит уведомление об обновлении контакта.
     */
    fun saveNotifyContact(contactNotifyDTO: ContactAddDTO) = GlobalScope.async(Dispatchers.IO) {
        val user = usersRepository.loadActualUser(contactNotifyDTO.id).await()
                ?: return@async

        addContactMark(user)
        notifyContactAdded(user)
    }

    /**
     * Убирает метку о том, что пользователь контакт и обновляет его, когда приходит уведомлении об удалении контакта.
     */
    fun removeNotifyContact(contactNotifyDTO: ContactRemoveDTO) = GlobalScope.async(Dispatchers.IO) {
        val user = usersRepository.loadActualUser(contactNotifyDTO.id).await()
                ?: return@async

        removeContactMark(user)
        notifyContactRemoved(user.uid)
    }

    /**
     * Обновляет контакты в списке, заменяя старые синхронизированными.
     *
     * @param contactsNotifyDTO - DTO со списком ID синхронизированных контактов.
     */
    fun syncNotifyContacts(contactsNotifyDTO: ContactsIdsListDTO) = GlobalScope.async(Dispatchers.IO) {
        contactsChannel.offer(fetchNewContacts(contactsNotifyDTO.ids) ?: return@async)
    }

    /**
     * Загружает контакты в соответствии с политикой:
     *
     * 1) Если есть возможность выполнить сетевой запрос, то выполняем его.
     * 2) Если нет возможности выполнить запрос по сети и данные в кэше не инициализированы, то грузим с БД
     * 3) В противном случае загоняем пустой список.
     */
    fun loadContacts() = GlobalScope.async(Dispatchers.IO) {
        if (authRepository.canExecuteNetworkRequest()) {
            contactsChannel.offer(fetchContacts() ?: return@async)
        } else if (contactsChannel.valueOrNull == null) {
            contactsChannel.offer(ArrayList(userDao.loadContacts()))
        }
    }

    /**
     * Загружает контакты с сети, если возникнет сетевая ошибка, то данные будут загружены с БД.
     */
    fun fetchContacts() = runBlocking {
        try {
            val contactsIdsListDTO = authRepository
                    .makeRequestWithSession<ContactsIdsListDTO>(protocolClient, "contacts.get", notifyToEventBus = false)
                    .await()

            // Contacts exists ...
            if (contactsIdsListDTO.isSuccess()) {
                return@runBlocking fetchNewContacts(contactsIdsListDTO.ids)
            } else if (contactsIdsListDTO.containsError() && !invalidateContacts()) {
                return@runBlocking ArrayList(userDao.loadContacts())
            }
        } catch (e: NetworkException) {
            return@runBlocking ArrayList(userDao.loadContacts())
        }

        return@runBlocking ArrayList<User>()
    }

    /**
     * Синхронизирует контакты с телефонной книгой.
     * В случае ошибки (кроме сетевой) все контакты будут удалены.
     */
    @Throws(InternalRequestException::class, RequestException::class)
    fun syncContacts() = GlobalScope.async(Dispatchers.IO) {
        val contactsFromPhone = context.loadContactsFromPhone()

        // Nothing to sync ...
        if (contactsFromPhone.isEmpty()) {
            throw InternalRequestException(InternalErrors.CONTACT_BOOK_IS_EMPTY)
        }

        try {
            val contactsSyncDTO = authRepository.makeRequestWithSession<ContactsSyncDTO>(protocolClient, "contacts.sync", ContactsSyncDTO().apply {
                items = contactsFromPhone
            }).await()

            if (contactsSyncDTO.isSuccess()) {
                val users = fetchNewContacts(contactsSyncDTO.ids) ?: return@async false

                // Notify subscribers about sync ...
                contactsChannel.offer(users)
                return@async true
            } else if (contactsSyncDTO.error == Errors.INVALID_USERS) {
                invalidateContacts(force = true)
                contactsChannel.offer(ArrayList())

                // Throw internal error ...
                throw InternalRequestException(InternalErrors.NO_SYNCED_CONTACTS)
            } else {
                throw RequestException(contactsSyncDTO.error)
            }
        } catch (e: NetworkException) {
            // Ignore
        }

        return@async false
    }

    /**
     * Добавляет пользователя в контакты.
     *
     * @param name - имя контакта
     * @param phone - телефон контакта
     */
    @Throws(RequestRegexException::class, RequestException::class, InternalRequestException::class)
    fun addContact(name: String, phone: String) = GlobalScope.async(Dispatchers.IO) {
        val regexFields = arrayListOf<Int>()
        val formattedName = name.trim().replace(WHITESPACES_REGEX, " ")
        val formattedPhone = formatPhone(phone, false)

        // Check formats ...
        if (formattedName.isNotEmpty() && !NAME_REGEX.matches(formattedName))
            regexFields.plusAssign(CONTACTS_NAME_REGEX_ERROR)
        if (formattedPhone.isEmpty() || !PHONE_REGEX.matches(phone))
            regexFields.plusAssign(CONTACTS_PHONE_REGEX_ERROR)
        if (regexFields.isNotEmpty()) throw RequestRegexException(regexFields)

        try {
            val contactAddDTO = authRepository.makeRequestWithSession<ContactAddDTO>(protocolClient, "contacts.add", ContactAddDTO().apply {
                this.name = formattedName
                this.phone = formattedPhone
            }).await()

            if (contactAddDTO.isSuccess()) {
                val user = usersRepository
                        .loadUser(contactAddDTO.id)
                        .await() ?: return@async false

                // Update ...
                user.name = formattedName
                user.phone = formattedPhone

                // Notify subscribers ...
                addContactMark(user)
                notifyContactAdded(user)
            } else if (contactAddDTO.error == Errors.INVALID_USER) {
                val account = accountRepository.getAccount() ?: return@async false
                val accountId = accountRepository.getAccountId(account) ?: return@async false
                val accountUser = usersRepository.loadUser(accountId).await() ?: return@async false

                if (accountUser.phone == phone) {
                    throw InternalRequestException(InternalErrors.ATTEMPT_TO_ADDING_MYSELF)
                } else if (userDao.isContactByPhone(phone)) {
                    throw InternalRequestException(InternalErrors.USER_ALREADY_ADDED)
                } else {
                    throw InternalRequestException(InternalErrors.USER_NOT_FOUND)
                }
            } else {
                throw RequestException(contactAddDTO.error)
            }

            return@async true
        } catch (e: NetworkException) {
            // Ignore
        }

        return@async false
    }

    /**
     * Удаляет пользователя из контактов
     *
     * @param id - ID пользователя, которого нужно удалить из контактов.
     */
    fun removeContact(id: Long) = GlobalScope.async(Dispatchers.IO) {
        try {
            authRepository.makeRequestWithSession<ContactRemoveDTO>(protocolClient, "contacts.remove", ContactRemoveDTO().apply {
                this.id = id
            }).await()

            // Remove users from storage & update it ...
            val user = usersRepository
                    .loadActualUser(id)
                    .await() ?: return@async false

            // Notify subscribers ...
            removeContactMark(user)
            notifyContactRemoved(id)

            // Request successfully completed!
            return@async true
        } catch (e: NetworkException) {
            // Ignore
        }

        return@async false
    }

    /**
     * Редактирует пользователя, находящегося в контактах.
     *
     * @param user - обьект пользователя с обновленными данными.
     */
    @Throws(InternalRequestException::class, RequestRegexException::class, RequestException::class)
    fun editContact(user: User) = GlobalScope.async(Dispatchers.IO) {
        val formattedName = user.name.trim().replace(WHITESPACES_REGEX, " ")

        // Check format ...
        if (formattedName.isNotEmpty() && !NAME_REGEX.matches(formattedName)) {
            throw RequestRegexException(arrayListOf(CONTACTS_NAME_REGEX_ERROR))
        }

        try {
            val contactEditDTO = authRepository.makeRequestWithSession<ContactEditDTO>(protocolClient, "contacts.edit", ContactEditDTO().apply {
                this.id = user.uid
                this.name = formattedName
            }).await()

            if (contactEditDTO.isSuccess()) {
                val actualUser = usersRepository
                        .loadUser(user.uid, onlyFromNetwork = formattedName.isEmpty())
                        .await() ?: return@async false

                // Save new data ...
                usersRepository.saveOrUpdateUsers(actualUser.apply {
                    name = if (formattedName.isNotEmpty()) formattedName else name
                }).await()

                // Notify subscribers ...
                notifyContactUpdated(actualUser)
            } else if (contactEditDTO.error == Errors.INVALID_USER) {
                val actualUser = usersRepository
                        .loadActualUser(user.uid)
                        .await() ?: return@async false

                // Notify subscribers ...
                removeContactMark(actualUser)
                notifyContactRemoved(actualUser.uid)

                // Throw error ...
                throw InternalRequestException(InternalErrors.USER_NOT_FOUND)
            } else {
                throw RequestException(contactEditDTO.error)
            }

            return@async true
        } catch (e: NetworkException) {
            // Ignore
        }

        return@async false
    }

    /**
     * Девалидирует старые контакты, удаляет их из кэша и загружает новые вместо них.
     * Если не удасться загрузить обьекты пользователей, то вернет null.
     *
     * @param ids - ID синхронизированных контактов/
     */
    fun fetchNewContacts(ids: ArrayList<Long>) = runBlocking {
        val invalidateResult = invalidateContacts(ids)

        // Error during removed contacts updating ...
        if (!invalidateResult) {
            return@runBlocking null
        }

        // Optimization: no needed to loading contacts if ids empty
        if (ids.isNotEmpty()) {
            val users = usersRepository
                    .loadActualUsers(ids)
                    .await()

            // Optimization: no needed adding contact mark to nothing
            if (users.isNotEmpty()) {
                addContactMark(*users.toTypedArray())
            } else {
                return@runBlocking null
            }

            // Return users ...
            return@runBlocking users
        } else {
            return@runBlocking ArrayList<User>()
        }
    }

    /**
     * Удаляет из кэша и обновляет пользователей, которые удалились из списка контактов.
     *
     * @param newContactsIds - список ID новых контактов.
     * @param force - удалять пользователей даже если не удалось их обновить?
     */
    fun invalidateContacts(newContactsIds: List<Long>? = null, force: Boolean = false) = runBlocking {
        val contacts = userDao.loadContacts()

        // Optimization: not needed to execute this code where contacts not exists
        if (contacts.isNotEmpty()) {
            val removedContacts = if (newContactsIds != null && newContactsIds.isNotEmpty()) {
                contacts.filter { !newContactsIds.contains(it.uid) }
            } else {
                contacts
            }

            // Optimization: not needed to execute this code where no one contacts removed
            if (removedContacts.isNotEmpty()) {
                val ids = removedContacts.map { it.uid }
                val users = usersRepository.loadActualUsers(ids).await()
                val success = users.isNotEmpty()

                // Remove contact mark ...
                if (force || success) {
                    removeContactMark(*removedContacts.toTypedArray())
                }

                // Return true only if users successfully updated
                return@runBlocking success
            }
        }

        // Or if no one users removed from contacts ;)
        return@runBlocking true
    }

    /**
     * Ставит пользователям метки о том, что они контакты и добавляет/обновляет их записи в БД.
     *
     * @param users - обьекты пользователей, которых нужно добавить в контакты.
     */
    fun addContactMark(vararg users: User) = runBlocking {
        // Optimization: no needed update user if it already marked as contact
        val needToUpdate = users
                .filter { !it.isContact }
                .apply { forEach { it.isContact = true } }

        if (needToUpdate.isNotEmpty()) {
            usersRepository
                    .saveOrUpdateUsers(*needToUpdate.toTypedArray())
                    .await()
        }
    }

    /**
     * Убирает у пользователя метку о том, что он контакт и добавляет/обновляет их записи в БД.
     *
     * @param users - обьекты пользователей, которые нужно удалить из контактов.
     */
    fun removeContactMark(vararg users: User) = runBlocking {
        // Optimization: no needed update user if it already not marked as contact
        val needToUpdate = users
                .filter { it.isContact }
                .apply { forEach { it.isContact = false } }

        if (needToUpdate.isNotEmpty()) {
            usersRepository
                    .saveOrUpdateUsers(*needToUpdate.toTypedArray())
                    .await()
        }
    }

    /**
     * Уведомляет слушателей, что был добавлен новый контакт.
     *
     * @param user - пользователь, добавленный в контакты.
     */
    fun notifyContactAdded(user: User) {
        if (contactsChannel.valueOrNull != null) {
            val contact = contactsChannel.value.find { it.uid == user.uid }

            if (contact == null) {
                contactsChannel.value.plusAssign(user)
                contactsChannel.offer(contactsChannel.value)
            }
        }
    }

    /**
     * Уведомляет слушателей, что контакт был обновлен.
     *
     * @param user - пользователь, запись в контактах которого была обновлена.
     */
    fun notifyContactUpdated(user: User) {
        if (contactsChannel.valueOrNull != null) {
            val index = contactsChannel.value.indexOfFirst { it.uid == user.uid }

            // Пользователь не найден в загруженном списке
            if (index == -1) return

            // Заменяем объект пользователя и уведомляем слушателей
            contactsChannel.value[index] = user
            contactsChannel.offer(contactsChannel.value)
        }
    }

    /**
     * Уведомляет слушателей, что контакт был удален.
     *
     * @param id - ID пользователя, которого удалили из контактов.
     */
    fun notifyContactRemoved(id: Long) {
        if (contactsChannel.valueOrNull != null) {
            val contact = contactsChannel.value.find { it.uid == id }

            if (contact != null) {
                contactsChannel.value.minusAssign(contact)
                contactsChannel.offer(contactsChannel.value)
            }
        }
    }
}