package com.sudox.android.ui.main.contacts

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.pm.PackageManager
import android.os.Build
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.repositories.main.ContactsRepository
import kotlinx.coroutines.*
import java.lang.Error
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val contactsRepository: ContactsRepository) : ViewModel() {

    var contactsLiveData: MutableLiveData<ArrayList<User>> = MutableLiveData()
    var contactsActionLiveData: MutableLiveData<ContactsAction> = MutableLiveData()
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

    private fun requestSyncContacts() = GlobalScope.launch {
        try {
            contactsRepository.syncContacts().await()
        } catch (e: InternalRequestException) {
            if (e.errorCode == InternalErrors.CONTACT_BOOK_IS_EMPTY) {
                contactsActionLiveData.postValue(ContactsAction.SHOW_NO_CONTACTS_IN_BOOK_DIALOG)
            }
        }
    }

    fun syncContacts(activity: Activity, permissionGranted: Boolean = false) {
        if (permissionGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            requestSyncContacts()
        } else {
            val grant = activity.checkSelfPermission(Manifest.permission.READ_CONTACTS)

            // Есть право на доступ к контактам.
            if (grant == PackageManager.PERMISSION_GRANTED) {
                requestSyncContacts()
            } else {
                activity.requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), CONTACT_SYNC_PERMISSION_REQUEST)
            }
        }
    }
}