package com.sudox.android.common.repository.main

import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.StateData
import com.sudox.android.common.enums.State
import com.sudox.android.common.models.dto.ContactDTO
import com.sudox.android.common.models.dto.ContactsGetDTO
import com.sudox.android.database.Contact
import com.sudox.android.database.ContactsDao
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.model.ResponseCallback
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class ContactsRepository(private val protocolClient: ProtocolClient,
                         private val contactsDao: ContactsDao) {

    val disposables: CompositeDisposable = CompositeDisposable()
    val contactsLoadLiveData = MutableLiveData<StateData<List<Contact>>>()
    val contactsUpdateLiveData = MutableLiveData<State>()

    fun initContactsListeners() {
        val mutableLiveData = MutableLiveData<State>()

        protocolClient.listenMessage("contacts.add", object : ResponseCallback<ContactDTO> {
            override fun onMessage(response: ContactDTO) {
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

        protocolClient.listenMessage("contacts.remove", object : ResponseCallback<ContactDTO> {
            override fun onMessage(response: ContactDTO) {
                contactsDao.deleteContactById(response.id)
                mutableLiveData.postValue(State.SUCCESS)
            }
        })
    }

    @Deprecated(message = "Replaced with new contacts loaded system.")
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

    private fun requestContactsFromDatabase(offset: Int, count: Int) {
        val disposable = contactsDao
                .getContacts(offset, count)
                .subscribe { contactsLoadLiveData.postValue(StateData(it, State.SUCCESS)) }

        disposables.add(disposable)
    }

    private fun requestContactsFromNetwork(offset: Int, count: Int) {
        val contactsGetDTO = ContactsGetDTO().apply {
            this.offset = offset
            this.count = count
        }

        if (!protocolClient.isConnected()) {
            contactsLoadLiveData.postValue(StateData(null, State.FAILED))
        } else {
            protocolClient.makeRequest("contacts.get", contactsGetDTO,
                    object : ResponseCallback<ContactsGetDTO> {
                        override fun onMessage(response: ContactsGetDTO) {
                            val contactsObjects = response.items
                            val contacts = arrayListOf<Contact>()

                            if (!response.isError() && response.code != 0 && contactsObjects != null) {
                                val length = contactsObjects.length()

                                for (i in 0 until length) {
                                    val contactDTO = ContactDTO().apply {
                                        fromJSON(contactsObjects.getJSONObject(i))
                                    }

                                    val contact = Contact(contactDTO.id,
                                            contactDTO.firstColor,
                                            contactDTO.secondColor,
                                            contactDTO.avatarUrl,
                                            contactDTO.name,
                                            contactDTO.nickname)

                                    contactsDao.insertContact(contact)
                                    contacts.plusAssign(contact)
                                }

                                contactsLoadLiveData.postValue(StateData(contacts, State.SUCCESS))
                            } else {
                                contactsLoadLiveData.postValue(StateData(null, State.FAILED))
                            }
                        }
                    })
        }
    }

    fun requestContacts(fromDatabase: Boolean, offset: Int, count: Int = 20) {
        if (fromDatabase) {
            requestContactsFromDatabase(offset, count)
        } else {
            requestContactsFromNetwork(offset, count)
        }
    }

    @Deprecated(message = "Replaced with new contacts loaded system.")
    fun requestAllContactsFromDB() {
//        //TODO: try to find the best way
//        Schedulers.io().scheduleDirect {
//            contactsLoadLiveData.postValue(contactsDao.getContacts())
//        }
    }
}