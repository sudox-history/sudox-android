package com.sudox.android.ui.main

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.users.AuthRepository
import com.sudox.android.ui.main.enums.MainActivityAction
import com.sudox.protocol.ProtocolClient
import com.sudox.android.common.helpers.livedata.SingleLiveEvent
import com.sudox.protocol.ConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val authRepository: AuthRepository,
                                        private val protocolClient: ProtocolClient) : ViewModel() {

    var accountSessionStateSubscription: ReceiveChannel<Boolean>? = null
    var connectionStateSubscription: ReceiveChannel<ConnectionState>? = null
    val mainActivityActionsLiveData = SingleLiveEvent<MainActivityAction>()

    /**
     * Метод для прослушки сессии и принятия необходимых решений.
     */
    fun listenSessionChanges() = GlobalScope.launch(Dispatchers.IO) {
        listenAccountSessionState()
        listenConnectionState()
    }

    private fun listenAccountSessionState() = GlobalScope.launch(Dispatchers.IO) {
        accountSessionStateSubscription = authRepository
                .accountSessionStateChannel
                .openSubscription()

        for (state in accountSessionStateSubscription!!) {
            if (!state) mainActivityActionsLiveData.postValue(MainActivityAction.SHOW_AUTH_ACTIVITY)
        }
    }

    private fun listenConnectionState() = GlobalScope.launch(Dispatchers.IO) {
        connectionStateSubscription = protocolClient
                .connectionStateChannel
                .openSubscription()

        for (state in connectionStateSubscription!!) {
            if(state == ConnectionState.OLD_PROTOCOL_VERSION)
                mainActivityActionsLiveData.postValue(MainActivityAction.SHOW_OLD_VERSION)
                break
        }
    }

    override fun onCleared() {
        accountSessionStateSubscription?.cancel()
        connectionStateSubscription?.cancel()
    }
}