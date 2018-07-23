package com.sudox.android.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.protocol.ProtocolClient

class SplashViewModel : ViewModel() {

    private var data: MutableLiveData<Boolean> = MutableLiveData()

    fun getData(): MutableLiveData<Boolean> = data

    fun connect() {
        val protocolClient = ProtocolClient()
        protocolClient.connect().subscribe {
            data.postValue(true)
        }
    }
}
