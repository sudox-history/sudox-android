package com.sudox.android.common.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.TokenState
import com.sudox.android.common.models.TokenData
import com.sudox.android.common.models.dto.AuthSessionDTO
import com.sudox.android.common.models.dto.SendCodeDTO
import com.sudox.android.common.models.dto.TokenDTO
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.model.ResponseCallback
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class AuthRepository @Inject constructor(private val protocolClient: ProtocolClient) {

    // Disposables list
    private var disposables: CompositeDisposable = CompositeDisposable()

    fun connect(): LiveData<Data<ConnectState>> {
        val connectData = MutableLiveData<Data<ConnectState>>()

        val disposable = protocolClient.connect().subscribe({
            connectData.postValue(Data(ConnectState.CONNECT))
        }, {
            connectData.postValue(Data(ConnectState.ERROR))
        })

        // Add to the list
        disposables.add(disposable)

        return connectData
    }

    fun startHandshake(): LiveData<Data<ConnectState>> {
        val handshakeData = MutableLiveData<Data<ConnectState>>()

        val disposable = protocolClient.startHandshake().subscribe({
            handshakeData.postValue(Data(ConnectState.SUCCESS_HANDSHAKE))
        }, {
            handshakeData.postValue(Data(ConnectState.FAILED_HANDSHAKE))
        })

        // Add to the list
        disposables.add(disposable)

        return handshakeData
    }

    fun sendToken(token: String?) : LiveData<TokenData> {
        val tokenData = MutableLiveData<TokenData>()

        if(token == null){
            tokenData.postValue(TokenData(TokenState.MISSING))
        } else {
            protocolClient.makeRequest("auth.importToken", TokenDTO(token), object : ResponseCallback<TokenDTO> {
                override fun onMessage(response: TokenDTO) {
                    if (response.code == 0)
                        tokenData.postValue(TokenData(TokenState.WRONG))
                    else tokenData.postValue(TokenData(TokenState.CORRECT, response.id))
                }
            })
        }
        return tokenData
    }

    fun sendEmail(email: String): LiveData<AuthSessionDTO> {
        val mutableLiveData = MutableLiveData<AuthSessionDTO>()

        protocolClient.makeRequest("auth.sendCode", SendCodeDTO(email), object : ResponseCallback<AuthSessionDTO> {
            override fun onMessage(response: AuthSessionDTO) {
                mutableLiveData.postValue(response)
            }
        })

        return mutableLiveData
    }

    fun cleanDisposables(){
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }
}