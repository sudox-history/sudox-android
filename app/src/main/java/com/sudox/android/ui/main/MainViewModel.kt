package com.sudox.android.ui.main

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.ui.main.enums.MainActivityAction
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    var accountSessionStateSubscription: ReceiveChannel<Boolean>? = null
    val mainActivityActionsLiveData = SingleLiveEvent<MainActivityAction>()

    /**
     * Метод для прослушки сессии и принятия необходимых решений.
     */
    fun listenSessionChanges() = GlobalScope.launch(Dispatchers.IO) {
        listenAccountSessionState()
    }

    private fun listenAccountSessionState() = GlobalScope.launch(Dispatchers.IO) {
        accountSessionStateSubscription = authRepository
                .accountSessionStateChannel
                .openSubscription()

        for (state in accountSessionStateSubscription!!) {
            if (!state) mainActivityActionsLiveData.postValue(MainActivityAction.SHOW_AUTH_ACTIVITY)
        }
    }

    override fun onCleared() {
        accountSessionStateSubscription?.cancel()
    }
}