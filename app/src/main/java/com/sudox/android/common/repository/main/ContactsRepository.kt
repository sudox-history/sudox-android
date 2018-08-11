package com.sudox.android.common.repository.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.enums.State
import com.sudox.android.common.models.dto.ContactsDTO
import com.sudox.android.database.Contact
import com.sudox.android.database.ContactsDao
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.model.ResponseCallback

class ContactsRepository(private val protocolClient: ProtocolClient,
                         private val contactsDao: ContactsDao) {

    fun initContactsListeners(): LiveData<State> {
        val mutableLiveData = MutableLiveData<State>()

        protocolClient.listenMessage("contacts.add", object : ResponseCallback<ContactsDTO> {
            override fun onMessage(response: ContactsDTO) {
                if (response.checkAvatar) {
                    contactsDao.insertContact(Contact(
                            response.id, response.avatarJson, null,
                            response.name, response.nickname)
                    )
                } else {
                    contactsDao.insertContact(Contact(response.id, null, response.avatarUrl, response.name, response.nickname))
                }

                mutableLiveData.postValue(State.SUCCESS)
            }
        })

        protocolClient.listenMessage("contacts.remove", object : ResponseCallback<ContactsDTO> {
            override fun onMessage(response: ContactsDTO) {
                contactsDao.deleteContactById(response.id)
                mutableLiveData.postValue(State.SUCCESS)
            }
        })

        return mutableLiveData
    }

    fun getAllContacts(): LiveData<List<Contact>> {
        return contactsDao.getAllContacts()
    }
}