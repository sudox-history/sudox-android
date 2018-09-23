package com.sudox.android.ui.main.contacts

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.main.ContactsRepository
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val contactsRepository: ContactsRepository) : ViewModel() {

//    fun contactsLoadLiveData() = contactsRepository.contactsLoadLiveData
////    fun contactsSearchUserByEmail(email: String) = contactsRepository.findUserByEmail(email)
//    fun contactAdd(id: String) = contactsRepository.contactAdd(id)
//    fun removeContact(id: String) = contactsRepository.removeContact(id)
}