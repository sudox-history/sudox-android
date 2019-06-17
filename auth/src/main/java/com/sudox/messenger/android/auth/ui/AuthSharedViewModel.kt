package com.sudox.messenger.android.auth.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.messenger.android.auth.data.AuthSession

class AuthSharedViewModel : ViewModel() {
    internal val sessionLiveData = MutableLiveData<AuthSession>()
}