package com.sudox.android.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.protocol.ProtocolClient
import io.reactivex.Completable

class SplashViewModel : ViewModel() {

    private var data: MutableLiveData<Boolean>? = null

    fun getData(): MutableLiveData<Boolean>? {
        if (data == null) {
            data = MutableLiveData()
        }

        return data
    }

    fun connect(): Completable {
        val protocolClient = ProtocolClient()
        return protocolClient.connect().doFinally {
            data!!.postValue(true)
        }
    }
}