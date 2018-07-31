package com.sudox.android.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.HandshakeState
import com.sudox.android.common.enums.TokenState
import com.sudox.android.common.models.TokenData
import com.sudox.android.common.models.dto.TokenDTO
import com.sudox.android.common.repository.AccountRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.model.ResponseCallback
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                          private val accountRepository: AccountRepository) : ViewModel() {

    var connectData = MutableLiveData<Data<ConnectState>>()
    var handshakeData = MutableLiveData<Data<HandshakeState>>()
    var tokenData = MutableLiveData<Data<TokenData>>()

    // Disposables list
    var disposables: CompositeDisposable = CompositeDisposable()

    fun connect() {
        val disposable = protocolClient.connect().subscribe({
            connectData.postValue(Data(ConnectState.SUCCESS))
        }, {
            connectData.postValue(Data(ConnectState.ERROR))
        })

        // Add to the list
        disposables.add(disposable)
    }

    fun startHandshake() {
        val disposable = protocolClient.startHandshake().subscribe({
            handshakeData.postValue(Data(HandshakeState.SUCCESS))
        }, {
            handshakeData.postValue(Data(HandshakeState.ERROR))
        })

        // Add to the list
        disposables.add(disposable)
    }

    fun sendToken() {
        val account = accountRepository.getAccount()

        // account == null -> token == null
        if (account == null) {
            tokenData.postValue(Data(TokenData(TokenState.MISSING)))
        } else {
            protocolClient.listenMessageOnce("auth.importToken", object : ResponseCallback<TokenDTO>{
                override fun onMessage(response: TokenDTO) {
                    if (response.code == 0)
                        tokenData.postValue(Data(TokenData(TokenState.WRONG)))
                    else tokenData.postValue(Data(TokenData(TokenState.CORRECT, response.id)))
                }
            })

            val token = TokenDTO()
            token.token = account.token
            protocolClient.sendMessage("auth.importToken", token)
        }
    }

    // Prevent memory leaks with protocol disposables
    override fun onCleared() {
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }
}
