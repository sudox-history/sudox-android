package com.sudox.android.ui.main.contacts

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.repositories.main.ContactsRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val contactsRepository: ContactsRepository) : ViewModel() {

    var contactsLiveData: MutableLiveData<List<User>> = MutableLiveData()

    fun loadContacts() = GlobalScope.launch {
        val contactsUsers = contactsRepository
                .loadContacts()
                .await()

        // To show
        contactsLiveData.postValue(contactsUsers)
    }
}