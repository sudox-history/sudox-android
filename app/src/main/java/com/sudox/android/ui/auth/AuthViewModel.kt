package com.sudox.android.ui.auth

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.android.data.repositories.users.AuthRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel @Inject constructor(val protocolClient: ProtocolClient,
                                        val authRepository: AuthRepository) : ViewModel() {

    private val subscriptions = SubscriptionsContainer()
    val authActivityEventsLiveData = MutableLiveData<AuthActivityEvent>()
    val authActivitySessionLiveData = MutableLiveData<AuthSession>()

    fun start() {
        listenConnectionStatus()
        listenAuthSession()
        listenAccountSessionState()
    }

    private fun listenAccountSessionState() = GlobalScope.launch(Dispatchers.IO) {
        for (state in subscriptions.addSubscription(authRepository
                .accountSessionStateChannel
                .openSubscription())) {

            if (state) {
                authActivityEventsLiveData.postValue(AuthActivityEvent.ACCOUNT_SESSION_STARTED)
            }
        }
    }

    private fun listenAuthSession() = GlobalScope.launch(Dispatchers.IO) {
        for (status in subscriptions.addSubscription(authRepository
                .authSessionChannel
                .openSubscription())) {

            if (status.status != AuthSession.AUTH_STATUS_UNDEFINED) {
                authActivitySessionLiveData.postValue(status)
            }
        }
    }

    private fun listenConnectionStatus() = GlobalScope.launch(Dispatchers.IO) {
        authActivityEventsLiveData.postValue(if (protocolClient.isValid()) {
            AuthActivityEvent.HANDSHAKE_SUCCEED
        } else {
            AuthActivityEvent.CONNECTION_CLOSED
        })

        for (state in subscriptions.addSubscription(protocolClient
                .connectionStateChannel
                .openSubscription())) {

            if (state == ConnectionState.CONNECTION_CLOSED) {
                authActivityEventsLiveData.postValue(AuthActivityEvent.CONNECTION_CLOSED)
            } else if (state == ConnectionState.HANDSHAKE_SUCCEED) {
                authActivityEventsLiveData.postValue(AuthActivityEvent.HANDSHAKE_SUCCEED)
            } else if (state == ConnectionState.OLD_PROTOCOL_VERSION) {
                authActivityEventsLiveData.postValue(AuthActivityEvent.SHOW_OLD_VERSION)
                break
            }
        }
    }

    override fun onCleared() {
        subscriptions.unsubscribeAll()
    }
}