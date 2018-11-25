package com.sudox.android.ui.main

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.chats.DialogsRepository
import com.sudox.android.ui.main.enums.MainActivityAction
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import com.sudox.protocol.models.enums.ConnectionState
import javax.inject.Inject

class MainViewModel @Inject constructor(private val authRepository: AuthRepository,
                                        private val dialogsRepository: DialogsRepository,
                                        private val protocolClient: ProtocolClient) : ViewModel() {

    // Шины ...
    val mainActivityActionsLiveData = SingleLiveEvent<MainActivityAction>()

    /**
     * Метод для прослушки сессии и принятия необходимых решений.
     */
    fun listenSessionChanges(lifecycleOwner: LifecycleOwner) {
        dialogsRepository.startWork()

        // Слушатель сессии
        authRepository.accountSessionLiveData.observe(lifecycleOwner, Observer {
            if (!it?.lived!!) mainActivityActionsLiveData.postValue(MainActivityAction.SHOW_AUTH_ACTIVITY)
        })
    }
}