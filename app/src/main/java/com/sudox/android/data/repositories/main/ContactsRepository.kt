package com.sudox.android.data.repositories.main

import android.arch.lifecycle.LiveData
import com.sudox.android.data.database.dao.ContactsDao
import com.sudox.android.data.database.model.Contact
import com.sudox.android.data.database.model.Contact.Companion.TRANSFORMATION_FROM_USER_INFO_DTO
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.contacts.dto.ContactChangeDTO
import com.sudox.android.data.models.contacts.dto.ContactsListDTO
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.experimental.async
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                             private val authRepository: AuthRepository,
                                             private val usersRepository: UsersRepository,
                                             private val contactsDao: ContactsDao) {

    val contactsLiveData: LiveData<List<Contact>> = contactsDao.getAll()

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
            deleteNotifyContact(it)
        }
    }

    /**
     * Получает пользователя по ID, пришедшему в уведомлении, маппит его до объекта контакта и сохраняет в БД
     **/
    private fun saveNotifyContact(contactNotifyDTO: ContactChangeDTO) = async {
        usersRepository.getUser(contactNotifyDTO.id) {
            contactsDao.insertOne(TRANSFORMATION_FROM_USER_INFO_DTO.invoke(it))
        }
    }

    /**
     * Удаляет пользователя с указанным ID из БД.
     * Если контакт с таким ID в БД не будет найден - ничего не произойдет.
     **/
    private fun deleteNotifyContact(contactNotifyDTO: ContactChangeDTO) = async {
        contactsDao.deleteOne(contactNotifyDTO.id)
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
     * Удаляет контакт. Если нет соединения с сервером - ничего не произойдет.
     **/
    fun deleteContact(id: String) {
        protocolClient.makeRequest<ContactChangeDTO>("contacts.remove", ContactChangeDTO().apply {
            this.id = id
        }) {
            if (it.isSuccess() || it.error == Errors.INVALID_USER) contactsDao.deleteOne(id)
        }
    }

    /**
     * Обновляет контакты в БД на основе переданного в аргументах списка.
     *
     * После обновления актуальная копия прилетит в LiveData.
     */
    private fun updateContactsInDatabase(contacts: List<Contact>) = async {
        contactsDao.deleteAll()

        // Сохраним контакты в БД
        if (contacts.isNotEmpty()) contactsDao.insertAll(contacts)
    }
}