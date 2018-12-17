package com.sudox.android.ui.main.contacts

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.pm.PackageManager
import android.os.Build
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

    companion object {
        const val CONTACT_SYNC_PERMISSION_REQUEST = 1
    }

    fun start() {
        listenContacts()
    }

    private fun listenContacts() = GlobalScope.launch(Dispatchers.IO) {
        for (contacts in subscriptionContainer
                .addSubscription(contactsRepository
                        .contactsChannel
                        .openSubscription())) {

            // Сортируем по имени (от А до Я, от A до Z и т.п.)
            contactsLiveData.postValue(ArrayList(contacts.sortedBy { it.name }))
        }
    }

    fun syncContacts(activity: Activity, permissionGranted: Boolean = false) {
        if (permissionGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            contactsRepository.syncContacts()
        } else {
            val grant = activity.checkSelfPermission(Manifest.permission.READ_CONTACTS)

            // Есть право на доступ к контактам.
            if (grant == PackageManager.PERMISSION_GRANTED) {
                contactsRepository.syncContacts()
            } else {
                activity.requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), CONTACT_SYNC_PERMISSION_REQUEST)
            }
        }
    }
}