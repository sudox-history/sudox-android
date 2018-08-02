package com.sudox.android.ui.auth

import androidx.lifecycle.ViewModel
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val protocolClient: ProtocolClient): ViewModel() {
    fun disconnect(){
        protocolClient.disconnect()
    }
}