package com.sudox.android.ui.main.messages.dialogs

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.models.common.LoadingType
import com.sudox.android.data.models.messages.chats.Dialog
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.chats.DialogsRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class DialogsViewModel @Inject constructor(val protocolClient: ProtocolClient,
                                           val authRepository: AuthRepository,
                                           val dialogsRepository: DialogsRepository) : ViewModel() {

    var initialDialogsLiveData: MutableLiveData<List<Dialog>> = SingleLiveEvent()
    var pagingDialogsLiveData: MutableLiveData<List<Dialog>> = SingleLiveEvent()

    // Subscriptions
    private val subscriptionsContainer = SubscriptionsContainer()

    fun start() = GlobalScope.launch(Dispatchers.IO) {
        GlobalScope.launch {
            subscriptionsContainer.addSubscription(dialogsRepository
                    .dialogsChannel
                    .openSubscription())
                    .consumeEach {
                        val loadingType = it.first
                        val dialogs = it.second

                        if (loadingType == LoadingType.INITIAL) {
                            initialDialogsLiveData.postValue(dialogs)
                        } else if (loadingType == LoadingType.PAGING) {
                            pagingDialogsLiveData.postValue(dialogs)
                        }
                    }
        }

        // Set callback to dialogs updating
        GlobalScope.launch {
            subscriptionsContainer.addSubscription(dialogsRepository
                    .dialogsUpdatesChannel
                    .openSubscription())
                    .consumeEach {
                        println("Sudox dialog update! ${it.user.name}")
                    }
        }

        // Start loading ...
        dialogsRepository.loadInitialDialogs()
    }

    fun loadPartOfDialogs(offset: Int) = GlobalScope.launch(Dispatchers.IO) {
        dialogsRepository.loadPagedDialogs(offset)
    }

    override fun onCleared() {
        subscriptionsContainer.unsubscribeAll()
    }
}