package com.sudox.android.common.repository.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.sudox.android.common.enums.UserSearchState
import com.sudox.android.common.enums.State
import com.sudox.android.common.models.UserSearchData
import com.sudox.android.common.models.dto.*
import com.sudox.android.common.repository.chat.MessagesRepository
import com.sudox.android.database.dao.ContactsDao
import com.sudox.android.database.model.Contact
import com.sudox.protocol.ProtocolClient

private val INVALID_PARAMETERS_ERROR = 50
private val INVALID_USER_ERROR = 51
private val EMPTY_CONTACTS_LIST_ERROR = 400

class ContactsRepository(private val protocolClient: ProtocolClient,
                         private val messagesRepository: MessagesRepository,
                         private val contactsDao: ContactsDao) {

    val contactsLoadLiveData = MutableLiveData<List<Contact>>()

    fun initContactsListeners() {
//        protocolClient.listenMessage<ContactNotifyAddRemoveDTO>("notify.contacts.add") {
//            if (it.errorCode != INVALID_USER_ERROR) {
//                val usersGetDTO = UsersGetDTO()
//                usersGetDTO.id = it.id
//
//                protocolClient.makeRequest<UsersGetDTO>("users.get", usersGetDTO) {
//                    if (it.errorCode != INVALID_USER_ERROR) {
//                        if (it.checkAvatar) {
//                            contactsDao.insertContact(Contact(
//                                    it.id, it.firstColor, it.secondColor, null,
//                                    it.name, it.nickname)
//                            )
//                        } else {
//                            contactsDao.insertContact(Contact(it.id, null, null,
//                                    it.avatarUrl, it.name, it.nickname))
//                        }
//
//                        requestAllContactsFromDB()
//                    }
//                }
//            }
//        }
//
//        protocolClient.listenMessage<ContactAddRemoveDTO>("contacts.remove") {
//            if (it.code != INVALID_USER_ERROR) {
//                contactsDao.deleteContactById(it.id)
//                messagesRepository.loadedContactsIds.minusAssign(it.id)
//                requestAllContactsFromDB()
//            }
//        }
    }

    fun getAllContactsFromServer(): LiveData<State> {
//        val mutableLiveData = MutableLiveData<State>()
//
//        protocolClient.makeRequest<ContactsGetDTO>("contacts.get", SimpleAnswerDTO()) {
//            if (it.errorCode != EMPTY_CONTACTS_LIST_ERROR) {
//                for (i in 0..(it.items!!.length() - 1)) {
//                    val item = it.items!!.getJSONObject(i)
//
//                    val contactsDTO = ContactDTO()
//                    contactsDTO.fromJSON(item)
//
//                    contactsDao.insertContact(Contact(contactsDTO.id, contactsDTO.firstColor,
//                            contactsDTO.secondColor, contactsDTO.avatarUrl, contactsDTO.name, contactsDTO.nickname))
//                }
//            } else {
//                contactsDao.deleteAllContacts()
//            }
//            mutableLiveData.postValue(State.SUCCESS)
//        }
//        return mutableLiveData
    }

    fun findUserByEmail(email: String): LiveData<UserSearchData> {
        val mutableLiveData = MutableLiveData<UserSearchData>()

        protocolClient.makeRequest<UserSearchDTO>("users.getByEmail", UserSearchDTO().apply {
            this.email = email
        }) {
            if (!it.containsError()) {

            } else if (it.errorCode == INVALID_USER_ERROR) {
                mutableLiveData.postValue(UserSearchData(UserSearchState.USER_DOES_NOT_EXIST))
            } else if (it.errorCode == INVALID_PARAMETERS_ERROR) {
                mutableLiveData.postValue(UserSearchData(UserSearchState.WRONG_FORMAT))
            }
        }

        return mutableLiveData
//        val mutableLiveData = MutableLiveData<UserSearchData>()
//
//        val contactSearchDTO = UserSearchDTO()
//        contactSearchDTO.email = email
//
//        protocolClient.makeRequest<UserSearchDTO>("users.getByEmail", contactSearchDTO) {
//            when {
//                it.errorCode == INVALID_USER_ERROR -> mutableLiveData.postValue(UserSearchData(UserSearchState.USER_DOES_NOT_EXIST))
//                it.errorCode == INVALID_PARAMETERS_ERROR -> mutableLiveData.postValue(UserSearchData(UserSearchState.WRONG_FORMAT))
//                else -> {
//                    val contact = if (it.checkAvatar) {
//                        Contact(it.scid, it.firstColor, it.secondColor,
//                                null, it.name, it.name)
//                    } else {
//                        Contact(it.scid, null, null,
//                                it.avatarUrl, it.name, it.name)
//                    }
//                    mutableLiveData.postValue(UserSearchData(UserSearchState.SUCCESS, contact))
//                }
//            }
//
//        }
//        return mutableLiveData
    }

    fun contactAdd(id: String) {
//        protocolClient.makeRequest<ContactAddRemoveDTO>("contacts.add", ContactAddRemoveDTO().apply {
//            sendId = id
//        }) {
//            if (!it.containsError()) {
//
//            } else if (it.errorCode == )
//        }
    }

    fun removeContact(id: String) {
//        val contactRemove = ContactAddRemoveDTO()
//        contactRemove.sendId = id
//        protocolClient.sendMessage("contacts.remove", contactRemove)
    }

    fun requestAllContactsFromDB() {
//        AsyncTask.execute {
//            contactsLoadLiveData.postValue(contactsDao.getContacts())
//        }
    }
}