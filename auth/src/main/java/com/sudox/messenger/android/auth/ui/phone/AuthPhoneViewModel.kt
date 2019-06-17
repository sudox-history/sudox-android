package com.sudox.messenger.android.auth.ui.phone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.messenger.android.auth.data.AuthSession

class AuthPhoneViewModel : ViewModel() {

    internal val errorsLiveData = MutableLiveData<Int>()

    fun requestCode(phone: String, sessionLiveData: MutableLiveData<AuthSession>) {
        sessionLiveData.postValue(AuthSession())
    }
}