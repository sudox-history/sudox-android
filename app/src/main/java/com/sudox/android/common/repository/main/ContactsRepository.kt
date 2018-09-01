package com.sudox.android.common.repository.main

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.enums.ContactSearchState
import com.sudox.android.common.enums.State
import com.sudox.android.common.models.ContactSearchData
import com.sudox.android.common.models.dto.*
import com.sudox.android.common.repository.chat.MessagesRepository
import com.sudox.android.database.dao.ContactsDao
import com.sudox.android.database.model.Contact
import com.sudox.protocol.ProtocolClient

class ContactsRepository(private val protocolClient: ProtocolClient,
                         private val messagesRepository: MessagesRepository,
                         private val contactsDao: ContactsDao) {

    val contactsLoadLiveData = MutableLiveData<List<Contact>>()

    fun initContactsListeners() {
        protocolClient.listenMessage<ContactAddRemoveDTO>("contacts.add") {
            if (it.errorCode != 402) {
                val usersGetDTO = UsersGetDTO()
                usersGetDTO.id = it.id

                protocolClient.makeRequest<UsersGetDTO>("users.get", usersGetDTO) {
                    if (it.checkAvatar) {
                        contactsDao.insertContact(Contact(
                                it.id, it.firstColor, it.secondColor, null,
                                it.name, it.nickname)
                        )
                    } else {
                        contactsDao.insertContact(Contact(it.id, null, null,
                                it.avatarUrl, it.name, it.nickname))
                    }

                    requestAllContactsFromDB()
                }
            }
        }

        protocolClient.listenMessage<ContactAddRemoveDTO>("contacts.remove") {
            if (it.code != 13) {
                contactsDao.deleteContactById(it.id)
                messagesRepository.loadedContactsIds.minusAssign(it.id)
                requestAllContactsFromDB()
            }
        }
    }

    fun getAllContactsFromServer(): LiveData<State> {
        val mutableLiveData = MutableLiveData<State>()

        protocolClient.makeRequest<ContactsGetDTO>("contacts.get", SimpleAnswerDTO()) {
            if (it.errorCode != 401) {
                for (i in 0..(it.items!!.length() - 1)) {
                    val item = it.items!!.getJSONObject(i)

                    val contactsDTO = ContactDTO()
                    contactsDTO.fromJSON(item)

                    contactsDao.insertContact(Contact(contactsDTO.id, contactsDTO.firstColor,
                            contactsDTO.secondColor, contactsDTO.avatarUrl, contactsDTO.name, contactsDTO.nickname))
                }
            } else {
                contactsDao.deleteAllContacts()
            }
            mutableLiveData.postValue(State.SUCCESS)
        }
        return mutableLiveData
    }

    fun findUserByEmail(email: String): LiveData<ContactSearchData> {
        val mutableLiveData = MutableLiveData<ContactSearchData>()

        val contactSearchDTO = ContactSearchDTO()
        contactSearchDTO.email = email

        protocolClient.makeRequest<ContactSearchDTO>("users.getByEmail", contactSearchDTO) {
            when {
                it.errorCode == 51 -> mutableLiveData.postValue(ContactSearchData(ContactSearchState.USER_DOES_NOT_EXIST))
                it.errorCode == 50 -> mutableLiveData.postValue(ContactSearchData(ContactSearchState.WRONG_FORMAT))
                else -> {
                    val contact = if (it.checkAvatar) {
                        Contact(it.scid, it.firstColor, it.secondColor,
                                null, it.name, it.name)
                    } else {
                        Contact(it.scid, null, null,
                                it.avatarUrl, it.name, it.name)
                    }
                    mutableLiveData.postValue(ContactSearchData(ContactSearchState.SUCCESS, contact))
                }
            }

        }
        return mutableLiveData
    }

    fun contactAdd(id: String) {
        val contactAddDTO = ContactAddRemoveDTO()
        contactAddDTO.sendId = id
        protocolClient.sendMessage("contacts.add", contactAddDTO)
    }

    fun removeContact(id: String) {
        val contactRemove = ContactAddRemoveDTO()
        contactRemove.sendId = id
        protocolClient.sendMessage("contacts.remove", contactRemove)
    }

    fun requestAllContactsFromDB() {
        AsyncTask.execute {
            contactsLoadLiveData.postValue(contactsDao.getContacts())
        }
    }


}