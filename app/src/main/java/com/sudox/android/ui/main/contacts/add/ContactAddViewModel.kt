package com.sudox.android.ui.main.contacts.add

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.models.Errors
import com.sudox.android.data.repositories.main.ContactsRepository
import com.sudox.android.ui.auth.register.enums.AuthRegisterAction
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.async
import javax.inject.Inject

class ContactAddViewModel @Inject constructor(val contactsRepository: ContactsRepository) : ViewModel() {

    val contactAddActionLiveData = SingleLiveEvent<ContactAddAction>()
    var contactAddRegexErrorsCallback: ((List<Int>) -> Unit)? = null
    val contactAddErrorsLiveData = SingleLiveEvent<Int>()

    fun addContact(name: String, phone: String) {
        contactsRepository.addContact(name, phone, {
            GlobalScope.async(Dispatchers.Main) { contactAddRegexErrorsCallback?.invoke(it) }
        }, {
            if (it == Errors.INVALID_USER) {
                contactAddActionLiveData.postValue(ContactAddAction.SHOW_USER_NOT_FOUND_ERROR)
            } else {
                contactAddErrorsLiveData.postValue(it)
            }
        }, {
            contactAddActionLiveData.postValue(ContactAddAction.POP_BACKSTACK)
        })
    }
}