package com.sudox.android.ui.main

import androidx.lifecycle.ViewModel
import com.sudox.android.common.repository.main.ContactsRepository
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val contactsRepository: ContactsRepository) : ViewModel() {

    fun contactCallback() = contactsRepository.mutableLiveData

    fun getAllContacts() = contactsRepository.getAllContacts()
}