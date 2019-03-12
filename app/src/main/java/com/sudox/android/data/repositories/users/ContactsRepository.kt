package com.sudox.android.data.repositories.users

import android.content.Context
import android.provider.ContactsContract
import android.text.TextUtils
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

const val CONTACTS_NAME_REGEX_ERROR = 0
const val CONTACTS_PHONE_REGEX_ERROR = 1

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
            loadSyncedContacts(it)
        }

        // Редактирование контакта
        protocolClient.listenMessage<ContactEditDTO>("updates.contacts.edit") {
            editNotifyContact(it)
        }
    }

    private fun listenSessionState() = GlobalScope.launch(Dispatchers.IO) {
        for (state in authRepository
                .accountSessionStateChannel
                .openSubscription()) {

            if (!state) {
                contactsChannel.clear()
            } else if (contactsChannel.valueOrNull != null) {
                loadContacts()
            }
        }
    }

    private fun editNotifyContact(contactEditDTO: ContactEditDTO) = GlobalScope.launch(Dispatchers.IO) {
        val user = usersRepository.loadUser(contactEditDTO.id, onlyFromNetwork = true).await()
                ?: return@launch

        addContactMark(user)
        notifyContactUpdated(user)
    }

    private fun saveNotifyContact(contactNotifyDTO: ContactAddDTO) = GlobalScope.launch(Dispatchers.IO) {
        val user = usersRepository.loadUser(contactNotifyDTO.id, onlyFromNetwork = true).await()
                ?: return@launch

        addContactMark(user)
        notifyContactAdded(user)
    }

    private fun removeNotifyContact(contactNotifyDTO: ContactRemoveDTO) = GlobalScope.launch(Dispatchers.IO) {
        val user = usersRepository.loadUser(contactNotifyDTO.id, onlyFromNetwork = true).await()
                ?: return@launch

        removeContactMark(user)
        notifyContactRemoved(user.uid)
    }

    fun loadContacts() = GlobalScope.launch(Dispatchers.IO) {
        // Send to subscribers ...
        contactsChannel.offer(if (authRepository.canExecuteNetworkRequest()) {
            fetchContacts()
        } else if (contactsChannel.valueOrNull == null) {
            ArrayList(userDao.loadContacts())
        } else {
            ArrayList()
        })
    }

    private suspend fun fetchContacts(): ArrayList<User> {
        try {
            val contactsIdsListDTO = authRepository
                    .makeRequestWithSession<ContactsIdsListDTO>(protocolClient, "contacts.get", notifyToEventBus = false)
                    .await()

            // Remove old contacts ...
            removeAllContacts()

            // Check that contacts exists
            if (contactsIdsListDTO.isSuccess()) {
                val users = usersRepository
                        .loadUsers(contactsIdsListDTO.ids)
                        .await()

                // Optimization: users can be empty, because connection dropped during request ...
                if (users.isNotEmpty()) {
                    return users.apply { addContactMark(*users.toTypedArray()) }
                }
            }
        } catch (e: NetworkException) {
            return ArrayList(userDao.loadContacts())
        }

        return ArrayList()
    }

    @Throws(InternalRequestException::class)
    fun syncContacts() = GlobalScope.async(Dispatchers.IO) {
        val contactsFromPhone = loadContactsFromPhone()

        // Nothing to sync ...
        if (contactsFromPhone.isEmpty()) {
            throw InternalRequestException(InternalErrors.CONTACT_BOOK_IS_EMPTY)
        }

        try {
            // All contacts will be removed even in case of error
            val contactsSyncDTO = authRepository.makeRequestWithSession<ContactsSyncDTO>(protocolClient, "contacts.sync", ContactsSyncDTO().apply {
                items = contactsFromPhone
            }).await()

            // Exclude situations where showing invalid contacts
            loadSyncedContacts(contactsSyncDTO).await()
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    @Throws(RequestRegexException::class, RequestException::class, InternalRequestException::class)
    fun addContact(name: String, phone: String) = GlobalScope.async(Dispatchers.IO) {
        val regexFields = arrayListOf<Int>()
        val formattedPhone = formatPhone(phone, false)
        val formattedName = name
                .trim()
                .replace(WHITESPACES_REGEX, " ")

        // Check formats ...
        if (formattedName.isNotEmpty() && !NAME_REGEX.matches(formattedName))
            regexFields.plusAssign(CONTACTS_NAME_REGEX_ERROR)
        if (phone.isEmpty() || !PHONE_REGEX.matches(phone)) regexFields.plusAssign(CONTACTS_PHONE_REGEX_ERROR)
        if (regexFields.isNotEmpty()) throw RequestRegexException(regexFields)

        try {
            val contactAddDTO = authRepository.makeRequestWithSession<ContactAddDTO>(protocolClient,"contacts.add", ContactAddDTO().apply {
                this.name = formattedName
                this.phone = formattedPhone
            }).await()

            if (contactAddDTO.isSuccess()) {
                val user = usersRepository
                        .loadUser(contactAddDTO.id, onlyFromNetwork = true)
                        .await() ?: return@async

                addContactMark(user)
                notifyContactAdded(user)
            } else if (contactAddDTO.error == Errors.INVALID_USER) {
                val account = accountRepository.getAccount() ?: return@async
                val accountId = accountRepository.getAccountId(account) ?: return@async
                val accountUser = usersRepository.loadUser(accountId).await() ?: return@async

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
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    fun removeContact(id: Long) = GlobalScope.launch(Dispatchers.IO) {
        try {
            authRepository.makeRequestWithSession<ContactRemoveDTO>(protocolClient,"contacts.remove", ContactRemoveDTO().apply {
                this.id = id
            }).await()

            // Excludes situations where user showing in contacts list but he isn't contact
            usersRepository
                    .removeUsers(id, updateExists = true)
                    .await()

            notifyContactRemoved(id)
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    @Throws(InternalRequestException::class, RequestRegexException::class, RequestException::class)
    fun editContact(user: User) = GlobalScope.async(Dispatchers.IO) {
        val formattedName = user.name
                .trim()
                .replace(WHITESPACES_REGEX, " ")

        // Check format ...
        if (formattedName.isNotEmpty() && !NAME_REGEX.matches(formattedName)) {
            throw RequestRegexException(arrayListOf(CONTACTS_NAME_REGEX_ERROR))
        }

        try {
            val contactEditDTO = authRepository.makeRequestWithSession<ContactEditDTO>(protocolClient, "contacts.edit", ContactEditDTO().apply {
                this.id = user.uid
                this.name = user.name
            }).await()

            // Excludes situations where user showing in contacts list but he isn't contact
            val updatedUser = usersRepository
                    .removeUsers(user.uid, updateExists = true)
                    .await()

            if (contactEditDTO.isSuccess() && updatedUser != null && updatedUser.isNotEmpty()) {
                notifyContactUpdated(updatedUser.first())
            } else if (contactEditDTO.error == Errors.INVALID_USER) {
                notifyContactRemoved(user.uid)

                // Error ...
                throw InternalRequestException(InternalErrors.USER_NOT_FOUND)
            } else {
                throw RequestException(contactEditDTO.error)
            }
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    private fun notifyContactAdded(user: User) {
        contactsChannel.value.plusAssign(user)
        contactsChannel.offer(contactsChannel.value)
    }

    private fun notifyContactUpdated(user: User) {
        val index = contactsChannel.value.indexOfFirst { it.uid == user.uid }

        // Пользователь не найден в загруженном списке
        if (index == -1) return

        // Заменяем объект пользователя и уведомляем слушателей
        contactsChannel.value[index] = user
        contactsChannel.offer(contactsChannel.value)
    }

    private fun notifyContactRemoved(id: Long) {
        val contact = contactsChannel.value.find { it.uid == id }

        if (contact != null) {
            contactsChannel.value.minusAssign(contact)
            contactsChannel.offer(contactsChannel.value)
        }
    }

    private suspend fun addContactMark(vararg users: User) {
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

    private suspend fun removeContactMark(user: User) {
        if (user.isContact) {
            user.isContact = false

            // Update ...
            usersRepository
                    .saveOrUpdateUsers(user)
                    .await()
        }
    }

    private fun loadContactsFromPhone(): ArrayList<ContactPairDTO> {
        val contactsPairs = ArrayList<ContactPairDTO>()
        val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

        val cursor = context
                .contentResolver
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null)

        if (cursor!!.count > 0) {
            val phoneColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val displayNameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val phone = cursor.getString(phoneColumnIndex)

                // Optimization: no needed parse it contact if phone empty
                if (TextUtils.isEmpty(phone)) {
                    continue
                }

                val formattedPhone = formatPhone(phone, false)

                // Optimization: no needed parse it contact if phone doesn't match the format or already added
                if (TextUtils.isEmpty(phone) || contactsPairs.find { it.phone == formattedPhone } != null) {
                    continue
                }

                val displayName = cursor.getString(displayNameColumnIndex)
                var contactName = if (!TextUtils.isEmpty(displayName)) {
                    displayName
                            .trim()
                            .replace(WHITESPACES_REGEX, " ")
                } else ""

                // Check name format
                if (!NAME_REGEX.matches(contactName)) {
                    contactName = ""
                }

                // Add new pair
                contactsPairs.plusAssign(ContactPairDTO().apply {
                    this.name = contactName
                    this.phone = formattedPhone
                })
            }
        }

        // Prevent memory leak
        cursor.close()

        // Return result
        return contactsPairs
    }

    private fun loadSyncedContacts(contactsIdsListDTO: ContactsIdsListDTO) = GlobalScope.async(Dispatchers.IO) {
        // Exclude situations where showing invalid contacts
        removeAllContacts()

        if (contactsIdsListDTO.isSuccess()) {
            val users = usersRepository
                    .loadUsers(contactsIdsListDTO.ids)
                    .await()

            addContactMark(*users.toTypedArray())
            contactsChannel.offer(users)
        } else {
            contactsChannel.offer(arrayListOf())
        }
    }

    private suspend fun removeAllContacts() {
        val contactsIds = userDao.loadContactsIds()

        // Remove all contacts users to exclude situations where displaying incorrect data
        if (contactsIds.isNotEmpty()) {
            usersRepository
                    .removeUsers(*contactsIds, updateExists = true)
                    .await()
        }
    }
}