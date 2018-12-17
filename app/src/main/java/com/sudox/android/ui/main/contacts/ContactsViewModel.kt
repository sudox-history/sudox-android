package com.sudox.android.ui.main.contacts

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.sudox.android.common.helpers.findAndRemoveIf
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.repositories.main.ContactsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val contactsRepository: ContactsRepository) : ViewModel() {

    var contactsLiveData: MutableLiveData<ArrayList<User>> = MutableLiveData()
    var subscriptionContainer = SubscriptionsContainer()

    fun start() {
        listenContacts()
        listenNewContacts()
        listenRemovedContacts()
    }

    private fun listenContacts() = GlobalScope.launch(Dispatchers.IO) {
        for (contacts in subscriptionContainer
                .addSubscription(contactsRepository
                        .contactsChannel
                        .openSubscription())) {

            // Сортируем по имени (от А до Я, от A до Z и т.п.)
            contacts.sortBy { it.name }

            // To show new contacts
            contactsLiveData.postValue(contacts)
        }
    }

    private fun listenNewContacts() = GlobalScope.launch(Dispatchers.IO) {
        for (contact in subscriptionContainer
                .addSubscription(contactsRepository
                        .newContactChannel
                        .openSubscription())) {

            // Заново сортируем ;(
            // TODO: Написать алгоритм для расчета места вставки, ибо этот код повторно сортирует весь массив
            contactsLiveData.postValue(contactsRepository
                    .contactsChannel
                    .value
                    .apply {
                        add(contact)
                        sortBy { it.name }
                    })
        }
    }

    private fun listenRemovedContacts() = GlobalScope.launch(Dispatchers.IO) {
        for (contactId in subscriptionContainer
                .addSubscription(contactsRepository
                        .removedContactIdChannel
                        .openSubscription())) {

            // Заново сортируем ;(
            // TODO: Написать алгоритм для расчета места вставки, ибо этот код повторно сортирует весь массив
            contactsLiveData.postValue(contactsRepository
                    .contactsChannel
                    .value
                    .apply {
                        findAndRemoveIf { it.uid == contactId }
                        sortBy { it.name }
                    })
        }
    }

    fun syncContacts(activity: Activity) {
        var grant = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)

        // Request
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOfNulls<String>(1)
            permissions[0] = Manifest.permission.READ_CONTACTS

            // Request
            ActivityCompat.requestPermissions(activity, permissions, 1)

            // Retry
            grant = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
        }

        // Start syncing ...
        if (grant == PackageManager.PERMISSION_GRANTED) {
            contactsRepository.syncContacts()
        }
    }
}