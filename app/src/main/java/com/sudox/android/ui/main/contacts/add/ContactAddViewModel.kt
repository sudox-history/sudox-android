package com.sudox.android.ui.main.contacts.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.exceptions.RequestException
import com.sudox.android.data.exceptions.RequestRegexException
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.repositories.users.ContactsRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactAddViewModel @Inject constructor(val contactsRepository: ContactsRepository) : ViewModel() {

    var contactAddActionsLiveData: MutableLiveData<ContactAddAction> = MutableLiveData()
    var contactAddRegexErrorsLiveData: MutableLiveData<ArrayList<Int>> = MutableLiveData()
    var contactAddErrorsLiveData: MutableLiveData<Int> = MutableLiveData()

    fun addContact(name: String, phone: String) = GlobalScope.launch {
        try {
            if (contactsRepository.addContact(name, phone).await()) {
                contactAddActionsLiveData.postValue(ContactAddAction.POP_BACKSTACK)
            }
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

    fun editContact(initialUser: User, editableUser: User) = GlobalScope.launch {
        // Save network, CPU and user time
        if (initialUser == editableUser) {
            contactAddActionsLiveData.postValue(ContactAddAction.POP_BACKSTACK)
            return@launch
        }

        // Обновляем
        try {
            contactsRepository
                    .editContact(editableUser)
                    .await()

            // Все ок! Возвращаемся обратно
            contactAddActionsLiveData.postValue(ContactAddAction.POP_BACKSTACK)
        } catch (e: RequestRegexException) {
            contactAddRegexErrorsLiveData.postValue(e.fields)
        } catch (e: RequestException) {
            contactAddErrorsLiveData.postValue(e.errorCode)
        } catch (e: InternalRequestException) {
            when {
                e.errorCode == InternalErrors.USER_NOT_FOUND ->
                    contactAddActionsLiveData.postValue(ContactAddAction.POP_BACKSTACK)
            }
        }
    }
}