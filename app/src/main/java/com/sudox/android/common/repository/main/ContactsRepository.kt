package com.sudox.android.common.repository.main

import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.enums.State
import com.sudox.android.common.models.dto.ContactsDTO
import com.sudox.android.common.models.dto.ContactsGetDTO
import com.sudox.android.database.Contact
import com.sudox.android.database.ContactsDao
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.model.ResponseCallback

class ContactsRepository(private val protocolClient: ProtocolClient,
                         private val contactsDao: ContactsDao) {

    val contactsLoadLiveData = MutableLiveData<List<Contact>>()
    val contactsUpdateLiveData = MutableLiveData<State>()

    fun initContactsListeners() {
        val mutableLiveData = MutableLiveData<State>()

        protocolClient.listenMessage("contacts.add", object : ResponseCallback<ContactsDTO> {
            override fun onMessage(response: ContactsDTO) {
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

        protocolClient.listenMessage("contacts.remove", object : ResponseCallback<ContactsDTO> {
            override fun onMessage(response: ContactsDTO) {
                contactsDao.deleteContactById(response.id)
                mutableLiveData.postValue(State.SUCCESS)
            }
        })
    }

    fun requestAllContacts() {
        val contactsGetDTO = ContactsGetDTO()
        contactsGetDTO.count = 10

        protocolClient.makeRequest("contacts.get", contactsGetDTO, object : ResponseCallback<ContactsGetDTO>{
            override fun onMessage(response: ContactsGetDTO) {

                if(response.code != 0) {
                    for (i in 0..(response.items!!.length() - 1)) {
                        val item = response.items!!.getJSONObject(i)

                        val contactsDTO = ContactsDTO()
                        contactsDTO.fromJSON(item)

                        contactsDao.insertContact(Contact(contactsDTO.id, contactsDTO.firstColor,
                                contactsDTO.secondColor, contactsDTO.avatarUrl, contactsDTO.name, contactsDTO.nickname))
                    }
                }
                contactsLoadLiveData.postValue(contactsDao.getAllContacts())

            }
        })
    }
}