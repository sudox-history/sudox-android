package com.sudox.android.data.repositories.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.sudox.android.common.userContact
import com.sudox.android.data.database.dao.UserDao
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.contacts.dto.ContactAddDTO
import com.sudox.android.data.models.contacts.dto.ContactRemoveDTO
import com.sudox.android.data.models.contacts.dto.ContactsListDTO
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsRepository @Inject constructor(val protocolClient: ProtocolClient,
                                             private val authRepository: AuthRepository,
                                             private val usersRepository: UsersRepository,
                                             private val userDao: UserDao) {

    val contactsGetLiveData: LiveData<List<User>> = userDao.getUserByType(userContact)

    init {
        // Обновим данные когда будет установлена сессия ...
        authRepository.accountSessionLiveData.observeForever {
            if (it?.lived!!) requestContacts()
        }

        // Добавление контактов.
        protocolClient.listenMessage<ContactAddDTO>("updates.importContact") {
            saveNotifyContact(it)
        }

        // Удаление контактов.
        protocolClient.listenMessage<ContactRemoveDTO>("updates.removeContact") {
            removeNotifyContact(it)
        }
    }

    /**
     * Получает пользователя по ID, пришедшему в уведомлении, маппит его до объекта контакта и сохраняет в БД
     **/
    private fun saveNotifyContact(contactNotifyDTO: ContactAddDTO) = GlobalScope.async {
        userDao.insertOne(User.TRANSFORMATION_FROM_CONTACT_CHANGE_DTO.invoke(contactNotifyDTO))
    }

    /**
     * Удаляет пользователя с указанным ID из БД.
     * Если контакт с таким ID в БД не будет найден - ничего не произойдет.
     **/
    private fun removeNotifyContact(contactNotifyDTO: ContactRemoveDTO) = GlobalScope.async {
        userDao.removeUserFromContacts(contactNotifyDTO.id, contactNotifyDTO.name)
    }

    /**
     * Обновляет копию контактов в БД до актуального состояния.
     * Если нет соединения с сервером, то контакты грузятся с локальной БД
     *
     * Последняя актуальная копия из БД всегда в LiveData.
     **/
    fun requestContacts() {
        // Получаем данные ...
        protocolClient.makeRequest<ContactsListDTO>("contacts.getContacts") {
            // Если будет UNAUTHORIZED, то выполнится перехват на глобальном уровне и произойдет сброс сессии
            if (it.isSuccess()) {
                updateContactsInDatabase(it.contacts.map(User.TRANSFORMATION_FROM_CONTACT_INFO_DTO))
            } else if (it.error == Errors.EMPTY_CONTACTS_LIST) {
                updateContactsInDatabase(emptyList())
            }
        }
    }

    /**
     * Добавляет контакт по ID, в случае ошибки на любом этапе возвращает errorCallback
     */
    fun addContact(name: String, phone: String): LiveData<Int> {
        val contactAddLiveData = MutableLiveData<Int>()

        protocolClient.makeRequest<ContactAddDTO>("contacts.importContact", ContactAddDTO().apply {
            this.name = name
            this.phone = phone
        }) {
            if (it.containsError()) contactAddLiveData.postValue(it.error)
            else {
                userDao.insertOne(User.TRANSFORMATION_FROM_CONTACT_CHANGE_DTO.invoke(it))
                contactAddLiveData.postValue(0)
            }
        }
        return contactAddLiveData
    }


    /**
     * Удаляет контакт. Если нет соединения с сервером - ничего не произойдет.
     **/
    fun removeContact(id: String) {
        protocolClient.makeRequest<ContactRemoveDTO>("contacts.removeContact", ContactRemoveDTO().apply {
            this.id = id
        }) {
            if (it.isSuccess() || it.error == Errors.INVALID_USER) {
                userDao.removeUserFromContacts(id, it.name)
            }
        }
    }

    /**
     * Обновляет контакты в БД на основе переданного в аргументах списка.
     *
     * После обновления актуальная копия прилетит в LiveData.
     */
    private fun updateContactsInDatabase(users: List<User>) = GlobalScope.async {
        userDao.removeAll()

        // Сохраним контакты в БД
        if (users.isNotEmpty()) userDao.insertAll(users)
    }

}