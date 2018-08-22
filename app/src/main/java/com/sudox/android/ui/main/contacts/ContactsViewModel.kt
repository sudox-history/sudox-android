package com.sudox.android.ui.main.contacts

import androidx.lifecycle.ViewModel
import com.sudox.android.common.repository.main.ContactsRepository
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val contactsRepository: ContactsRepository) : ViewModel() {

    fun contactsUpdateLiveData() = contactsRepository.contactsUpdateLiveData
    fun contactsLoadLiveData() = contactsRepository.contactsLoadLiveData
    fun contactsSearchUserByNickname(nickname: String) = contactsRepository.findUserByNickname(nickname)
    fun contactAdd(id: String) = contactsRepository.contactAdd(id)
}