package com.sudox.android.ui.main.contacts

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.ContactsRepository
import javax.inject.Inject

class ContactsViewModel @Inject constructor(val authRepository: AuthRepository,
                                            val contactsRepository: ContactsRepository) : ViewModel() {
}