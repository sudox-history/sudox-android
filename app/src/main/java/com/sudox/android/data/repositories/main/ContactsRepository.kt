package com.sudox.android.data.repositories.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.sudox.android.data.database.dao.ContactsDao
import com.sudox.android.data.database.model.Contact
import com.sudox.android.data.database.model.Contact.Companion.TRANSFORMATION_FROM_USER_INFO_DTO
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.contacts.dto.ContactChangeDTO
import com.sudox.android.data.models.contacts.dto.ContactsListDTO
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                             private val authRepository: AuthRepository,
                                             private val usersRepository: UsersRepository,
                                             private val contactsDao: ContactsDao) {

    val contactsGetLiveData: LiveData<List<Contact>> = contactsDao.loadAll()
    val contactSearchLiveData = MutableLiveData<Contact>()

    init {
        // Обновим данные когда будет установлена сессия ...
        authRepository.accountSessionLiveData.observeForever {
            if (it?.lived!!) requestContacts()
        }

        // Добавление контактов.
        protocolClient.listenMessage<ContactChangeDTO>("notify.contacts.new") {
            saveNotifyContact(it)
        }

        // Удаление контактов.
        protocolClient.listenMessage<ContactChangeDTO>("notify.contacts.remove") {
            removeNotifyContact(it)
        }
    }

    /**
     * Получает пользователя по ID, пришедшему в уведомлении, маппит его до объекта контакта и сохраняет в БД
     **/
    private fun saveNotifyContact(contactNotifyDTO: ContactChangeDTO) = GlobalScope.async {
        usersRepository.getUser(contactNotifyDTO.id) {
            contactsDao.insertOne(TRANSFORMATION_FROM_USER_INFO_DTO.invoke(it))
        }
    }

    /**
     * Удаляет пользователя с указанным ID из БД.
     * Если контакт с таким ID в БД не будет найден - ничего не произойдет.
     **/
    private fun removeNotifyContact(contactNotifyDTO: ContactChangeDTO) = GlobalScope.async {
        contactsDao.removeOne(contactNotifyDTO.id)
    }

    /**
     * Обновляет копию контактов в БД до актуального состояния.
     * Если нет соединения с сервером, то контакты грузятся с локальной БД
     *
     * Последняя актуальная копия из БД всегда в LiveData.
     **/
    fun requestContacts() {
        // Получаем данные ...
        protocolClient.makeRequest<ContactsListDTO>("contacts.get") {
            // Если будет UNAUTHORIZED, то выполнится перехват на глобальном уровне и произойдет сброс сессии
            if (it.isSuccess()) {
                updateContactsInDatabase(it.contacts.map(Contact.TRANSFORMATION_FROM_CONTACT_INFO_DTO))
            } else if (it.error == Errors.EMPTY_CONTACTS_LIST) {
                updateContactsInDatabase(emptyList())
            }
        }
    }

    /**
     * Метод поиска контакта по E-mail
     */
    fun searchContactByEmail(email: String, errorCallback: (Int) -> Unit) {
        usersRepository.getUserByEmail(email) {
            if(it.isSuccess()) {
                contactSearchLiveData.postValue(Contact.TRANSFORMATION_FROM_USER_GET_BY_EMAIL_DTO(it))
            } else {
                errorCallback(it.error)
            }
        }
    }

    /**
     * Добавляет контакт по ID, в случае ошибки на любом этапе возвращает errorCallback
     */
    fun addContact(id: String, errorCallback: (Int) -> (Unit)) {
        protocolClient.makeRequest<ContactChangeDTO>("contacts.new", ContactChangeDTO().apply {
            this.id = id
        }) {
            if (it.isSuccess()) {
                usersRepository.getUser(id) { userInfoDTO ->
                    if (it.isSuccess()) {
                        contactsDao.insertOne(Contact.TRANSFORMATION_FROM_USER_INFO_DTO.invoke(userInfoDTO))
                    } else {
                        errorCallback(it.error)
                    }
                }
            } else {
                errorCallback(it.error)
            }
        }
    }

    /**
     * Удаляет контакт. Если нет соединения с сервером - ничего не произойдет.
     **/
    fun removeContact(id: String) {
        protocolClient.makeRequest<ContactChangeDTO>("contacts.remove", ContactChangeDTO().apply {
            this.id = id
        }) {
            if (it.isSuccess() || it.error == Errors.INVALID_USER) {
                contactsDao.removeOne(id)
            }
        }
    }

    /**
     * Обновляет контакты в БД на основе переданного в аргументах списка.
     *
     * После обновления актуальная копия прилетит в LiveData.
     */
    private fun updateContactsInDatabase(contacts: List<Contact>) = GlobalScope.async {
        contactsDao.removeAll()

        // Сохраним контакты в БД
        if (contacts.isNotEmpty()) contactsDao.insertAll(contacts)
    }
}