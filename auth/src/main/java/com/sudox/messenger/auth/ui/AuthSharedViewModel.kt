package com.sudox.messenger.auth.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.messenger.auth.data.AuthSession

class AuthSharedViewModel : ViewModel() {
    internal val sessionLiveData = MutableLiveData<AuthSession>()
}