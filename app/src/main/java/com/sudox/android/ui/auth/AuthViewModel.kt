package com.sudox.android.ui.auth

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.models.auth.state.AuthSession
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.filter
import javax.inject.Inject

class AuthViewModel @Inject constructor(val protocolClient: ProtocolClient,
                                        val authRepository: AuthRepository) : ViewModel() {

    private val subscriptions = SubscriptionsContainer()
    val authActivityEventsLiveData = SingleLiveEvent<AuthActivityEvent>()
    val authActivitySessionLiveData = SingleLiveEvent<AuthSession>()

    fun start() {
        GlobalScope.launch(Dispatchers.IO) {
            subscriptions.addSubscription(protocolClient
                    .connectionStateChannel
                    .openSubscription())
                    .consumeEach {
                        if (it == ConnectionState.CONNECTION_CLOSED) {
                            authActivityEventsLiveData.postValue(AuthActivityEvent.CONNECTION_CLOSED)
                        } else if (it == ConnectionState.HANDSHAKE_SUCCEED) {
                            authActivityEventsLiveData.postValue(AuthActivityEvent.HANDSHAKE_SUCCEED)
                        }
                    }
        }

        GlobalScope.launch(Dispatchers.IO) {
            subscriptions.addSubscription(authRepository
                    .authSessionChannel
                    .openSubscription())
                    .filter { it.status != AuthSession.AUTH_STATUS_UNDEFINED }
                    .consumeEach { authActivitySessionLiveData.postValue(it) }
        }

        GlobalScope.launch(Dispatchers.IO) {
            subscriptions.addSubscription(authRepository
                    .accountSessionStateChannel
                    .openSubscription())
                    .filter { it }
                    .consumeEach { authActivityEventsLiveData.postValue(AuthActivityEvent.ACCOUNT_SESSION_STARTED) }
        }
    }

    override fun onCleared() {
        subscriptions.unsubscribeAll()
    }
}