package com.sudox.android.ui.main.messages.dialogs

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.models.common.LoadingType
import com.sudox.android.data.models.messages.dialogs.Dialog
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.dialogs.DialogsRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class DialogsViewModel @Inject constructor(val protocolClient: ProtocolClient,
                                           val authRepository: AuthRepository,
                                           val dialogsRepository: DialogsRepository) : ViewModel() {

    var initialDialogsLiveData: MutableLiveData<ArrayList<Dialog>> = SingleLiveEvent()
    var pagingDialogsLiveData: MutableLiveData<List<Dialog>> = SingleLiveEvent()
    var subscriptionsContainer: SubscriptionsContainer = SubscriptionsContainer()
    var isListNotEmpty: Boolean = false

    init {
        listenAuthSession()
    }

    private fun listenAuthSession() = GlobalScope.launch(Dispatchers.IO) {
        for (state in subscriptionsContainer.addSubscription(authRepository
                .accountSessionStateChannel
                .openSubscription())) {

            if (state && !isListNotEmpty) loadDialogs()
        }
    }

    fun loadDialogs(offset: Int = 0) = GlobalScope.launch(Dispatchers.IO) {
        if (protocolClient.isValid() && !authRepository.sessionIsValid) {
            // Авторизация ещё не прошла. Подгрузка будет вызвана, когда произойдет авторизация
            // Если список не пустой, обновление будет выполнено путем "вставок"
            return@launch
        }

        val dialogs = dialogsRepository
                .loadDialogs(offset)
                .await()

        if (offset == 0) {
            initialDialogsLiveData.postValue(dialogs)
        } else {
            pagingDialogsLiveData.postValue(dialogs)
        }
    }
}