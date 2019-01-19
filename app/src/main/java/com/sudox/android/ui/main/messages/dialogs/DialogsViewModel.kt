package com.sudox.android.ui.main.messages.dialogs

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.models.messages.dialogs.Dialog
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.dialogs.DialogsRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.*
import javax.inject.Inject

class DialogsViewModel @Inject constructor(val protocolClient: ProtocolClient,
                                           val authRepository: AuthRepository,
                                           val dialogsRepository: DialogsRepository) : ViewModel() {

    var initialDialogsLiveData: MutableLiveData<ArrayList<Dialog>> = SingleLiveEvent()
    var pagingDialogsLiveData: MutableLiveData<List<Dialog>> = SingleLiveEvent()

    private var subscriptionsContainer: SubscriptionsContainer = SubscriptionsContainer()
    private var isLoading: Boolean = false
    private var isListEnded: Boolean = false
    private var lastLoadedOffset: Int = 0
    private var loadedDialogsCount: Int = 0

    init {
        listenAccountSession()
    }

    private fun listenAccountSession() = GlobalScope.launch {
        for (state in subscriptionsContainer
                .addSubscription(authRepository
                        .accountSessionStateChannel
                        .openSubscription())) {

            // Если не успеем подгрузить с сети во время загрузки фрагмента.
            if (state) {
                if (loadedDialogsCount == 0) {
                    loadDialogs()
                } else {
                    updateDialogs()
                }
            }
        }
    }

    private fun updateDialogs() = GlobalScope.launch(Dispatchers.IO) {
        isLoading = true
        isListEnded = false

        if (loadedDialogsCount <= 20) {
            val dialogs = dialogsRepository
                    .loadDialogs(0, loadedDialogsCount, onlyFromNetwork = true)
                    .await()

            lastLoadedOffset = 0
            loadedDialogsCount = dialogs.size
            initialDialogsLiveData.postValue(dialogs)
        } else {
            var currentLoaded = 0
            val dialogs = ArrayList<Dialog>()
            val neededParts = Math.ceil(loadedDialogsCount / 20.0)

            for (i in 0 until neededParts.toInt()) {
                try {
                    val part = dialogsRepository
                            .loadDialogs(currentLoaded, 20, onlyFromNetwork = true)
                            .await()

                    if (part.isNotEmpty()) {
                        dialogs.plusAssign(part)
                        currentLoaded += dialogs.size
                        lastLoadedOffset = currentLoaded
                    } else if (part.size < 20) {
                        break
                    }
                } catch (e: InternalRequestException) {
                    if (e.errorCode == InternalErrors.LIST_ENDED) {
                        isListEnded = true
                        break
                    }
                }
            }

            loadedDialogsCount = dialogs.size
            initialDialogsLiveData.postValue(dialogs)
        }

        isLoading = false
    }

    fun loadDialogs(offset: Int = 0, limit: Int = 20) {
        if (isLoading || isListEnded || offset in 1..lastLoadedOffset) return

        // Загрузим диалоги ...
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Блокируем дальнейшие действия
                isLoading = true

                // Запрашиваем список диалогов ...
                val dialogs = dialogsRepository
                        .loadDialogs(offset, limit)
                        .await()

                if (offset == 0) {
                    initialDialogsLiveData.postValue(dialogs)
                    loadedDialogsCount = dialogs.size
                } else {
                    pagingDialogsLiveData.postValue(dialogs)
                    loadedDialogsCount += dialogs.size
                }

                lastLoadedOffset = offset
            } catch (e: InternalRequestException) {
                if (e.errorCode == InternalErrors.LIST_ENDED) {
                    isListEnded = true
                }
            }

            // Загрузка завершена, разблокируем дальнейшие действия.
            isLoading = false
        }
    }

    override fun onCleared() {
        subscriptionsContainer.unsubscribeAll()
    }
}