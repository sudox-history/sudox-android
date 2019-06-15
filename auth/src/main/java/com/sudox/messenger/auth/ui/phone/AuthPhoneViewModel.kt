package com.sudox.messenger.auth.ui.phone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthPhoneViewModel : ViewModel() {

    internal val errorsLiveData = MutableLiveData<Int>()

    fun requestCode(phone: String) {
    }
}