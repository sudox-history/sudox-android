package com.sudox.android.ui.main

import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.ContactsRepository
import com.sudox.android.data.repositories.messages.chats.DialogsRepository
import com.sudox.android.ui.main.enums.MainActivityAction
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.filter
import javax.inject.Inject

class MainViewModel @Inject constructor(private val authRepository: AuthRepository,
                                        private val dialogsRepository: DialogsRepository,
                                        private val contactsRepository: ContactsRepository) : ViewModel() {

    var accountSessionStateSubscription: ReceiveChannel<Boolean>? = null
    val mainActivityActionsLiveData = SingleLiveEvent<MainActivityAction>()

    /**
     * Метод для прослушки сессии и принятия необходимых решений.
     */
    fun listenSessionChanges() = GlobalScope.launch(Dispatchers.IO) {
        dialogsRepository.startWork()
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