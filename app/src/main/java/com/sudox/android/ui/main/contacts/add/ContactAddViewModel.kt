package com.sudox.android.ui.main.contacts.add

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.main.ContactsRepository
import com.sudox.protocol.models.SingleLiveEvent
import javax.inject.Inject

class ContactAddViewModel @Inject constructor(val contactsRepository: ContactsRepository) : ViewModel() {

}