package com.sudox.android.ui.main

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.repositories.main.ContactsRepository
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.ChatRepository
import com.sudox.android.ui.main.enums.MainActivityAction
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import javax.inject.Inject

class MainViewModel @Inject constructor(private val protocolClient: ProtocolClient,
                                        private val contactsRepository: ContactsRepository,
                                        private val chatRepository: ChatRepository,
                                        private val accountRepository: AccountRepository,
                                        private val authRepository: AuthRepository) : ViewModel() {

    // Шины ...
    val connectionStateLiveData = protocolClient.connectionStateLiveData
    val mainActivityActionsLiveData = SingleLiveEvent<MainActivityAction>()

    /**
     * Метод для прослушки сессии и принятия необходимых решений.
     */
    fun listenSessionChanges(lifecycleOwner: LifecycleOwner) {
        authRepository.accountSessionLiveData.observe(lifecycleOwner, Observer {
            if (!it?.lived!!) mainActivityActionsLiveData.postValue(MainActivityAction.SHOW_AUTH_ACTIVITY)
        })
    }
}