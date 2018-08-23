package com.sudox.android.common.repository.main

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.enums.State
import com.sudox.android.common.models.dto.*
import com.sudox.android.database.Contact
import com.sudox.android.database.ContactsDao
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.model.ResponseCallback

class ContactsRepository(private val protocolClient: ProtocolClient,
                         private val contactsDao: ContactsDao) {

    val contactsLoadLiveData = MutableLiveData<List<Contact>>()

    fun initContactsListeners() {
        protocolClient.listenMessage("contacts.add", object : ResponseCallback<ContactAddRemoveDTO> {
            override fun onMessage(response: ContactAddRemoveDTO) {
                if (response.code != 0) {
                    val usersGetDTO = UsersGetDTO()
                    usersGetDTO.id = response.id

                    protocolClient.makeRequest("users.get", usersGetDTO, object : ResponseCallback<UsersGetDTO> {
                        override fun onMessage(response: UsersGetDTO) {
                            if (response.checkAvatar) {
                                contactsDao.insertContact(Contact(
                                        response.id, response.firstColor, response.secondColor, null,
                                        response.name, response.nickname)
                                )
                            } else {
                                contactsDao.insertContact(Contact(response.id, null, null,
                                        response.avatarUrl, response.name, response.nickname))
                            }

                            requestAllContactsFromDB()
                        }
                    })
                }
            }
        })

        protocolClient.listenMessage("contacts.remove", object : ResponseCallback<ContactAddRemoveDTO> {
            override fun onMessage(response: ContactAddRemoveDTO) {
                if(response.code != 0) {
                    contactsDao.deleteContactById(response.id)
                    requestAllContactsFromDB()
                }
            }
        })
    }

    fun getAllContactsFromServer(): LiveData<State> {
        val mutableLiveData = MutableLiveData<State>()

        protocolClient.makeRequest("contacts.get", SimpleAnswerDTO(), object : ResponseCallback<ContactsGetDTO> {
            override fun onMessage(response: ContactsGetDTO) {
                if (response.code != 0) {
                    for (i in 0..(response.items!!.length() - 1)) {
                        val item = response.items!!.getJSONObject(i)

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
        })
        return mutableLiveData
    }

    fun findUserByNickname(nickname: String): LiveData<Contact?> {
        val mutableLiveData = MutableLiveData<Contact?>()

        val contactSearchDTO = ContactSearchDTO()
        contactSearchDTO.nickname = nickname

        protocolClient.makeRequest("users.search", contactSearchDTO, object : ResponseCallback<ContactSearchDTO> {
            override fun onMessage(response: ContactSearchDTO) {
                if (response.code != 0) {
                    val contact = if (response.checkAvatar) {
                        Contact(response.scid, response.firstColor, response.secondColor,
                                null, response.name, response.name)
                    } else {
                        Contact(response.scid, null, null,
                                response.avatarUrl, response.name, response.name)
                    }
                    mutableLiveData.postValue(contact)
                } else {
                    mutableLiveData.postValue(null)
                }

            }

        })
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
        Handler().post{
            contactsLoadLiveData.postValue(contactsDao.getContacts())
        }
    }


}