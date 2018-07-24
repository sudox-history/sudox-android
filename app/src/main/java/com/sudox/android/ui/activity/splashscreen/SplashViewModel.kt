package com.sudox.android.ui.activity.splashscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.enums.HandshakeState
import com.sudox.android.common.enums.TokenState
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class SplashViewModel @Inject constructor(private val protocolClient: ProtocolClient) : ViewModel() {

    var connectData = MutableLiveData<Data<ConnectState>>()
    var handshakeData = MutableLiveData<Data<HandshakeState>>()
    var tokenData = MutableLiveData<Data<TokenState>>()

    fun connect() {
        protocolClient.connect().subscribe({
            connectData.postValue(Data(ConnectState.SUCCESS))
        },{
            connectData.postValue(Data(ConnectState.ERROR))
        })
    }

    fun startHandshake(){
        protocolClient.startHandshake().subscribe({
            handshakeData.postValue(Data(HandshakeState.SUCCESS))
        },{
            handshakeData.postValue(Data(HandshakeState.ERROR))
        })
    }

    fun sendToken(token: String?){
        if(token == null){
            tokenData.postValue(Data(TokenState.MISSING))
        }
    }
}
