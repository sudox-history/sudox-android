package com.sudox.android.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sudox.android.common.Data
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.repository.main.ContactsRepository
import com.sudox.protocol.ProtocolClient
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MainViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                        private val contactsRepository: ContactsRepository) : ViewModel() {

    var connectLiveData = MutableLiveData<Data<ConnectState>>()

    // Connection controller
    private var connectionDisposable: Disposable = protocolClient.connectionSubject.subscribe {
        connectLiveData.postValue(Data(it))
    }

    fun initContactsListeners() {
        contactsRepository.initContactsListeners()
    }

    fun disconnect() = protocolClient.disconnect()

    override fun onCleared() {
        connectionDisposable.dispose()
        super.onCleared()
    }
}