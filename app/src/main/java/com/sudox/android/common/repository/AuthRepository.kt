package com.sudox.android.common.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.models.dto.AuthSessionDTO
import com.sudox.android.common.models.dto.SendCodeDTO
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.model.ResponseCallback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val protocolClient: ProtocolClient) {

    fun sendEmail(email: String) : LiveData<AuthSessionDTO> {
        val mutableLiveData = MutableLiveData<AuthSessionDTO>()

        protocolClient.makeRequest("auth.sendCode", SendCodeDTO(email), object : ResponseCallback<AuthSessionDTO> {
            override fun onMessage(response: AuthSessionDTO) {
                mutableLiveData.postValue(response)
            }
        })

        return mutableLiveData
    }
}