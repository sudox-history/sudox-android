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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class DialogsViewModel @Inject constructor(val protocolClient: ProtocolClient,
                                           val authRepository: AuthRepository,
                                           val dialogsRepository: DialogsRepository) : ViewModel() {

    var initialDialogsLiveData: MutableLiveData<ArrayList<Dialog>> = SingleLiveEvent()
    var pagingDialogsLiveData: MutableLiveData<List<Dialog>> = SingleLiveEvent()

    private var subscriptionsContainer: SubscriptionsContainer = SubscriptionsContainer()
    private var isLoading: Boolean = false
    private var isListEnded: Boolean = false
    private var isListNotEmpty: Boolean = false
    private var lastLoadedOffset: Int = 0

    init {
        listenAccountSession()
    }


    private fun listenAccountSession() = GlobalScope.launch {
        for (state in subscriptionsContainer
                .addSubscription(authRepository
                        .accountSessionStateChannel
                        .openSubscription())) {

            // Если не успеем подгрузить с сети во время загрузки фрагмента.
            if (state && !isListNotEmpty) loadDialogs()
        }
    }

    fun loadDialogs(offset: Int = 0) {
        if (isLoading || isListEnded || offset > 0 && offset <= lastLoadedOffset) return

        // Загрузим диалоги ...
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Блокируем дальнейшие действия
                isLoading = true

                // Запрашиваем список диалогов ...
                val dialogs = dialogsRepository
                        .loadDialogs(offset)
                        .await()

                if (dialogs.isNotEmpty() && offset > 0) {
                    isListNotEmpty = true
                }

                if (offset == 0) {
                    initialDialogsLiveData.postValue(dialogs)
                } else {
                    pagingDialogsLiveData.postValue(dialogs)
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