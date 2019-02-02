package com.sudox.android.data.repositories.main

import android.content.Context
import android.provider.ContactsContract
import com.sudox.android.common.helpers.NAME_REGEX
import com.sudox.android.common.helpers.PHONE_REGEX
import com.sudox.android.common.helpers.WHITESPACES_REMOVE_REGEX
import com.sudox.android.common.helpers.findAndRemoveIf
import com.sudox.android.data.database.dao.user.UserDao
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.exceptions.RequestRegexException
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.models.contacts.dto.*
import com.sudox.android.data.models.users.UserType
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.dialogs.DialogsMessagesRepository
import com.sudox.android.data.repositories.messages.dialogs.DialogsRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.NetworkException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import javax.inject.Inject
import javax.inject.Singleton

const val CONTACTS_NAME_REGEX_ERROR = 0
const val CONTACTS_PHONE_REGEX_ERROR = 1

@Singleton
class ContactsRepository @Inject constructor(val protocolClient: ProtocolClient,
                                             private val authRepository: AuthRepository,
                                             private val usersRepository: UsersRepository,
                                             private val dialogsMessagesRepository: DialogsMessagesRepository,
                                             private val dialogsRepository: DialogsRepository,
                                             private val userDao: UserDao,
                                             private val context: Context) {

    val contactsChannel: ConflatedBroadcastChannel<ArrayList<User>> = ConflatedBroadcastChannel()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            // Загрузим начальную копию с БД
            contactsChannel.offer(ArrayList(userDao.loadByType(UserType.CONTACT)))

            // Слушаем сессию ...
            for (state in authRepository
                    .accountSessionStateChannel
                    .openSubscription()) {

                if (state) {
                    requestContacts() // Load new contacts
                } else {
                    contactsChannel.offer(ArrayList()) // Clean RAM cache
                }
            }
        }

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
            loadContactsBeforeSync(it)
        }

        // Редактирование контакта
        protocolClient.listenMessage<ContactEditDTO>("updates.contacts.edit") {
            editNotifyContact(it)
        }
    }

    private fun editNotifyContact(contactEditDTO: ContactEditDTO) = GlobalScope.launch(Dispatchers.IO) {
        // P.S.: Либо выполнится запрос к серверу и юзер будет сохран как контакт, либо в БД данный юзер просто будет помечен как контакт.
        val user = usersRepository.loadUser(contactEditDTO.id, UserType.CONTACT, onlyFromNetwork = true).await()
                ?: return@launch

        usersRepository
                .saveOrUpdateUser(user)
                .await()

        // Обновление в UI
        notifyContactUpdated(user)
    }

    private fun saveNotifyContact(contactNotifyDTO: ContactAddDTO) = GlobalScope.launch(Dispatchers.IO) {
        // P.S.: Либо выполнится запрос к серверу и юзер будет сохран как контакт, либо в БД данный юзер просто будет помечен как контакт.
        val user = usersRepository.loadUser(contactNotifyDTO.id, UserType.CONTACT, onlyFromNetwork = true).await()
                ?: return@launch

        // На отображение в UI ...
        notifyContactAdded(user)
    }

    private fun removeNotifyContact(contactNotifyDTO: ContactRemoveDTO) = GlobalScope.launch(Dispatchers.IO) {
        // Снимаем метку о том, что юзер есть в контактах.
        userDao.setType(contactNotifyDTO.id, UserType.UNKNOWN)

        // Апдейт ;)
        notifyContactRemoved(contactNotifyDTO.id)
    }

    private fun requestContacts() = GlobalScope.launch(Dispatchers.IO) {
        try {
            val contactsIdsListDTO = protocolClient
                    .makeRequest<ContactsIdsListDTO>("contacts.get")
                    .await()

            // Чистим список контактов
            userDao.setUnknownByType(UserType.CONTACT) // Скроем все ...

            // Контактов нет
            if (contactsIdsListDTO.error == Errors.EMPTY_CONTACTS_LIST) {
                contactsChannel.offer(arrayListOf()) // Обновим список в UI ...
            } else if (contactsIdsListDTO.containsError()) {
                contactsChannel.offer(arrayListOf())
            } else {
                contactsChannel.offer(ArrayList(usersRepository
                        .loadUsers(contactsIdsListDTO.ids, UserType.CONTACT)
                        .await()))
            }
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    private fun loadContactsBeforeSync(contactsSyncDTO: ContactsIdsListDTO) = GlobalScope.launch(Dispatchers.IO) {
        try {
            // Чистим список контактов
            userDao.setUnknownByType(UserType.CONTACT) // Скроем все ...

            // Контактов нет
            if (contactsSyncDTO.error == Errors.INVALID_USERS) {
                contactsChannel.offer(arrayListOf()) // Обновим список в UI ...
            } else if (contactsSyncDTO.containsError()) {
                contactsChannel.offer(arrayListOf())
            } else {
                val users = usersRepository
                        .loadUsers(contactsSyncDTO.ids, UserType.CONTACT, true)
                        .await()

                // Обновим контакт в чате
                if (dialogsMessagesRepository.openedDialogRecipientId != 0L) {
                    var user = users.find { it.uid == dialogsMessagesRepository.openedDialogRecipientId }

                    if (user == null) {
                        user = usersRepository
                                .loadUser(dialogsMessagesRepository.openedDialogRecipientId)
                                .await()

                        if (user != null) {
                            dialogsMessagesRepository.dialogRecipientUpdateChannel?.offer(user)
                        }
                    } else {
                        dialogsMessagesRepository.dialogRecipientUpdateChannel?.offer(user)
                    }
                }

                dialogsRepository.dialogRecipientsUpdatesChannel.offer(ArrayList(users))
                contactsChannel.offer(ArrayList(users))
            }
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    @Throws(RequestRegexException::class, RequestException::class, InternalRequestException::class)
    fun addContact(name: String, phone: String) = GlobalScope.async(Dispatchers.IO) {
        val filteredName = name.trim().replace(WHITESPACES_REMOVE_REGEX, " ")
        val regexFields = arrayListOf<Int>()

        if (filteredName.isNotEmpty() && !NAME_REGEX.matches(filteredName))
            regexFields.plusAssign(CONTACTS_NAME_REGEX_ERROR)
        if (!PHONE_REGEX.matches(phone)) regexFields.plusAssign(CONTACTS_PHONE_REGEX_ERROR)
        if (regexFields.isNotEmpty()) throw RequestRegexException(regexFields)

        try {
            val contactAddDTO = protocolClient.makeRequestWithControl<ContactAddDTO>("contacts.add", ContactAddDTO().apply {
                this.name = filteredName
                this.phone = phone
            }).await()

            if (contactAddDTO.isSuccess()) {
                // При загрузке пользователь будет отмечен и сохранен в БД как контакт.
                val user = usersRepository
                        .loadUser(contactAddDTO.id, UserType.CONTACT, onlyFromNetwork = true)
                        .await() ?: return@async

                // Обновление в UI
                notifyContactAdded(user)
            } else if (contactAddDTO.error == Errors.INVALID_USER) {
                val accountUser = usersRepository
                        .getAccountUser()
                        .await() ?: return@async

                // Попытка добавить самого себя ;(
                if (accountUser.phone == phone) {
                    throw InternalRequestException(InternalErrors.ATTEMPT_TO_ADDING_MYSELF)
                } else if (userDao.loadByTypeAndPhone(phone, UserType.CONTACT) != null) {
                    throw InternalRequestException(InternalErrors.USER_ALREADY_ADDED)
                } else {
                    throw InternalRequestException(InternalErrors.USER_NOT_FOUND)
                }
            } else {
                throw RequestException(contactAddDTO.error)
            }
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    fun removeContact(id: Long) = GlobalScope.launch(Dispatchers.IO) {
        try {
            val contactRemoveDTO = protocolClient
                    .makeRequestWithControl<ContactRemoveDTO>("contacts.remove", ContactRemoveDTO().apply {
                        this.id = id
                    }).await()

            if (!(contactRemoveDTO.isSuccess() || contactRemoveDTO.error == Errors.INVALID_USER))
                return@launch

            // Removing ...
            usersRepository.removeUser(id)
            notifyContactRemoved(id)
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    @Throws(InternalRequestException::class, RequestRegexException::class, RequestException::class)
    fun editContact(user: User) = GlobalScope.async(Dispatchers.IO) {
        val filteredName = user.name.trim().replace(WHITESPACES_REMOVE_REGEX, " ")
        val regexFields = arrayListOf<Int>()

        if (filteredName.isNotEmpty() && !NAME_REGEX.matches(filteredName))
            regexFields.plusAssign(CONTACTS_NAME_REGEX_ERROR)
        if (regexFields.isNotEmpty()) throw RequestRegexException(regexFields)

        try {
            val contactEditDTO = protocolClient.makeRequestWithControl<ContactEditDTO>("contacts.edit", ContactEditDTO().apply {
                id = user.uid
                name = user.name
            }).await()

            if (contactEditDTO.isSuccess()) {
                usersRepository
                        .saveOrUpdateUser(user)
                        .await()

                // Обновление в UI
                notifyContactUpdated(user)
            } else if (contactEditDTO.error == Errors.INVALID_USER) {
                // Юзера больше нет в контактах/в Sudox. Удаляем из БД
                usersRepository.removeUser(user.uid)
                notifyContactRemoved(user.uid)

                // Возвращаем ошибку
                throw InternalRequestException(InternalErrors.USER_NOT_FOUND)
            } else {
                throw RequestException(contactEditDTO.error)
            }
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    @Throws(InternalRequestException::class)
    fun syncContacts() = GlobalScope.async(Dispatchers.IO) {
        val phoneContacts = loadContactsFromPhone()

        // Nothing to sync ...
        if (phoneContacts.isEmpty()) throw InternalRequestException(InternalErrors.CONTACT_BOOK_IS_EMPTY)

        // Will be remove all contacts & save the new
        try {
            val contactsSyncDTO = protocolClient.makeRequestWithControl<ContactsSyncDTO>("contacts.sync", ContactsSyncDTO().apply {
                items = phoneContacts
            }).await()

            // Load new contacts ...
            loadContactsBeforeSync(contactsSyncDTO)
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    private fun loadContactsFromPhone(): ArrayList<ContactPairDTO> {
        context
                .contentResolver
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
                .use {
                    val nameColumnIndex = it!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val hasPhoneNumberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)
                    val phoneColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val pairs = arrayListOf<ContactPairDTO>()

                    while (it.moveToNext()) {
                        if (it.getInt(hasPhoneNumberIndex) == 0) continue

                        val name = it.getString(nameColumnIndex)
                        var phone = it.getString(phoneColumnIndex)
                                .replace(")", "")
                                .replace("(", "")
                                .replace(" ", "")
                                .replace("+", "")
                                .replace("-", "")

                        // 89674788147 -> 79674788147
                        if (phone.startsWith("8"))
                            phone = "7${phone.substring(1)}"

                        // Валидация
                        if (!NAME_REGEX.matches(name) || !PHONE_REGEX.matches(phone))
                            continue

                        val contact = ContactPairDTO().apply {
                            this.name = name
                            this.phone = phone
                        }

                        if(pairs.find { contact.phone == it.phone } != null)
                            continue
                        else pairs.plusAssign(contact)
                    }

                    return pairs
                }
    }

    private fun notifyContactAdded(user: User) {
        contactsChannel.value.plusAssign(user)
        contactsChannel.offer(contactsChannel.value)

        // Обновим юзера в чате
        if (dialogsMessagesRepository.openedDialogRecipientId == user.uid) {
            dialogsMessagesRepository.dialogRecipientUpdateChannel?.offer(user)
        }

        dialogsRepository.dialogRecipientsUpdatesChannel.offer(listOf(user))
    }

    private fun notifyContactUpdated(user: User) {
        val index = contactsChannel.value.indexOfFirst { it.uid == user.uid }

        // Пользователь не найден в загруженном списке
        if (index == -1) return

        // Заменяем объект пользователя и уведомляем слушателей
        contactsChannel.value[index] = user
        contactsChannel.offer(contactsChannel.value)

        // Обновим юзера в чате
        if (dialogsMessagesRepository.openedDialogRecipientId == user.uid) {
            dialogsMessagesRepository.dialogRecipientUpdateChannel?.offer(user)
        }

        dialogsRepository.dialogRecipientsUpdatesChannel.offer(listOf(user))
    }

    private suspend fun notifyContactRemoved(id: Long) {
        contactsChannel.value.findAndRemoveIf { it.uid == id }
        contactsChannel.offer(contactsChannel.value)

        val user = usersRepository
                .loadUser(id, onlyFromNetwork = true)
                .await() ?: return

        // Грузим юзера для апдейта ...
        if (dialogsMessagesRepository.openedDialogRecipientId == id) {
            dialogsMessagesRepository.dialogRecipientUpdateChannel?.offer(user)
        }

        dialogsRepository.dialogRecipientsUpdatesChannel.offer(listOf(user))
    }
}