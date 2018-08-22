package com.sudox.android.common.repository.main

import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.enums.State
import com.sudox.android.common.models.dto.*
import com.sudox.android.database.Contact
import com.sudox.android.database.ContactsDao
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.model.ResponseCallback
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ContactsRepository(private val protocolClient: ProtocolClient,
                         private val contactsDao: ContactsDao) {

    val contactsLoadLiveData = MutableLiveData<List<Contact>>()
    val contactsUpdateLiveData = MutableLiveData<State>()

    fun initContactsListeners() {
        val mutableLiveData = MutableLiveData<State>()

        protocolClient.listenMessage("contacts.add", object : ResponseCallback<ContactAddDTO> {
            override fun onMessage(response: ContactAddDTO) {
                if (response.code != 0) {
                    val usersGetDTO = UsersGetDTO()
                    usersGetDTO.id = response.aid

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

                            mutableLiveData.postValue(State.SUCCESS)
                        }
                    })
                } else {
                    mutableLiveData.postValue(State.FAILED)
                }
            }
        })

        protocolClient.listenMessage("contacts.remove", object : ResponseCallback<ContactDTO> {
            override fun onMessage(response: ContactDTO) {
                contactsDao.deleteContactById(response.id)
                mutableLiveData.postValue(State.SUCCESS)
            }
        })
    }

    fun getAllContactsFromServer(): Single<State> = Single.unsafeCreate {
        val contactsGetDTO = ContactsGetDTO()
        contactsGetDTO.count = 10

        protocolClient.makeRequest("contacts.get", contactsGetDTO, object : ResponseCallback<ContactsGetDTO> {
            override fun onMessage(response: ContactsGetDTO) {
                if (response.code != 0) {
                    for (i in 0..(response.items!!.length() - 1)) {
                        val item = response.items!!.getJSONObject(i)

                        val contactsDTO = ContactDTO()
                        contactsDTO.fromJSON(item)

                        contactsDao.insertContact(Contact(contactsDTO.id, contactsDTO.firstColor,
                                contactsDTO.secondColor, contactsDTO.avatarUrl, contactsDTO.name, contactsDTO.nickname))
                    }
                }
                it.onSuccess(State.SUCCESS)
            }
        })
    }

    fun findUserByNickname(nickname: String): MutableLiveData<Contact?> {
        val mutableLiveData = MutableLiveData<Contact?>()

        val contactSearchDTO = ContactSearchDTO()
        contactSearchDTO.nickname = nickname

        protocolClient.makeRequest("users.getByNickname", contactSearchDTO, object : ResponseCallback<ContactSearchDTO> {
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
        val contactAddDTO = ContactAddDTO()
        contactAddDTO.id = id
        protocolClient.sendMessage("contacts.add", contactAddDTO)
    }

    fun requestAllContactsFromDB() {
//        //TODO: try to find the best way
        Schedulers.io().scheduleDirect {
            contactsLoadLiveData.postValue(contactsDao.getContacts())
        }
    }
}