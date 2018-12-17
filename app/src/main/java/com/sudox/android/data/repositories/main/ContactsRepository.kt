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
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
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
                                             private val userDao: UserDao,
                                             private val context: Context) {

    val contactsChannel: ConflatedBroadcastChannel<ArrayList<User>> = ConflatedBroadcastChannel()
    val newContactChannel: ConflatedBroadcastChannel<User> = ConflatedBroadcastChannel()
    val removedContactIdChannel: ConflatedBroadcastChannel<Long> = ConflatedBroadcastChannel()

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
    }

    private fun saveNotifyContact(contactNotifyDTO: ContactAddDTO) = GlobalScope.launch(Dispatchers.IO) {
        // P.S.: Либо выполнится запрос к серверу и юзер будет сохран как контакт, либо в БД данный юзер просто будет помечен как контакт.
        val user = usersRepository.loadUser(contactNotifyDTO.id, UserType.CONTACT).await()
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
                        .loadUser(contactAddDTO.id, UserType.CONTACT)
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
            userDao.setType(id, UserType.UNKNOWN)
            notifyContactRemoved(id)
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    fun syncContacts() = GlobalScope.launch(Dispatchers.IO) {
        val phoneContacts = loadContactsFromPhone()

        // Nothing to sync ...
        if (phoneContacts.isEmpty()) return@launch

        // Will be remove all contacts & save the new
        try {
            val contactsSyncDTO = protocolClient.makeRequestWithControl<ContactsSyncDTO>("contacts.sync", ContactsSyncDTO().apply {
                items = phoneContacts
            }).await()

            // Load new contacts ...
            if (contactsSyncDTO.isSuccess()) requestContacts()
        } catch (e: NetworkException) {
            // Ignore
        }
    }

    fun loadContactsFromPhone(): ArrayList<ContactPairDTO> {
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

                        pairs.plusAssign(ContactPairDTO().apply {
                            phone = it.getString(phoneColumnIndex)
                            name = it.getString(nameColumnIndex)
                        })
                    }

                    return pairs
                }
    }

    private suspend fun notifyContactAdded(user: User) {
        contactsChannel.valueOrNull?.plusAssign(user)
        newContactChannel.send(user)
    }

    private suspend fun notifyContactRemoved(id: Long) {
        contactsChannel.valueOrNull?.findAndRemoveIf { it.uid == id }
        removedContactIdChannel.send(id)
    }
}