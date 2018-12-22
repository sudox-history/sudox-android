package com.sudox.android.ui.main.contacts.add

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.exceptions.RequestRegexException
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.repositories.main.ContactsRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactAddViewModel @Inject constructor(val contactsRepository: ContactsRepository) : ViewModel() {

    var contactAddActionsLiveData: MutableLiveData<ContactAddAction> = MutableLiveData()
    var contactAddRegexErrorsLiveData: MutableLiveData<ArrayList<Int>> = MutableLiveData()
    var contactAddErrorsLiveData: MutableLiveData<Int> = MutableLiveData()

    fun addContact(name: String, phone: String) = GlobalScope.launch {
        try {
            contactsRepository
                    .addContact(name, phone)
                    .await()

            // Все ок! Возвращаемся обратно
            contactAddActionsLiveData.postValue(ContactAddAction.POP_BACKSTACK)
        } catch (e: RequestRegexException) {
            contactAddRegexErrorsLiveData.postValue(e.fields)
        } catch (e: RequestException) {
            contactAddErrorsLiveData.postValue(e.errorCode)
        } catch (e: InternalRequestException) {
            when {
                e.errorCode == InternalErrors.ATTEMPT_TO_ADDING_MYSELF ->
                    contactAddActionsLiveData.postValue(ContactAddAction.SHOW_ATTEMPT_TO_ADDING_MYSELF_ERROR)
                e.errorCode == InternalErrors.USER_ALREADY_ADDED ->
                    contactAddActionsLiveData.postValue(ContactAddAction.SHOW_USER_ALREADY_ADDED_ERROR)
                e.errorCode == InternalErrors.USER_NOT_FOUND ->
                    contactAddActionsLiveData.postValue(ContactAddAction.SHOW_USER_NOT_FOUND_ERROR)
            }
        }
    }
}