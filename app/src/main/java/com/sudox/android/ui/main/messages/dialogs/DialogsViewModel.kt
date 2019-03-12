package com.sudox.android.ui.main.messages.dialogs

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sudox.android.common.helpers.livedata.ActiveSingleLiveEvent
import com.sudox.android.common.helpers.livedata.SingleLiveEvent
import com.sudox.android.data.SubscriptionsContainer
import com.sudox.android.data.database.model.messages.DialogMessage
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.models.messages.dialogs.Dialog
import com.sudox.android.data.repositories.users.AuthRepository
import com.sudox.android.data.repositories.messages.dialogs.DialogsRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.*
import javax.inject.Inject

class DialogsViewModel @Inject constructor(val protocolClient: ProtocolClient,
                                           val authRepository: AuthRepository,
                                           val dialogsRepository: DialogsRepository) : ViewModel() {

    var initialDialogsLiveData: MutableLiveData<ArrayList<Dialog>> = SingleLiveEvent()
    var pagingDialogsLiveData: MutableLiveData<List<Dialog>> = SingleLiveEvent()
    var movesToTopMessagesLiveData: ActiveSingleLiveEvent<DialogMessage> = ActiveSingleLiveEvent()
    var movesToTopDialogsLiveData: ActiveSingleLiveEvent<Dialog> = ActiveSingleLiveEvent()
    var recipientsUpdatesLiveData: ActiveSingleLiveEvent<List<User>> = ActiveSingleLiveEvent()

    private var subscriptionsContainer: SubscriptionsContainer = SubscriptionsContainer()
    private var dialogsCount: Int = 0
    private var dialogsEnded: Boolean = false

    // Jobs ...
    private var initJob: Job? = null
    private var updateJob: Job? = null
    private var pagingJob: Job? = null

    init {
        // Фильтр для сообщений (в очереди на обновление, может находится только одно сообщение из каждого диалога!)
        movesToTopMessagesLiveData.filter = { current, new ->
            current.getRecipientId() == new!!.getRecipientId() && new.date >= current.date
        }

        // Фильтр для сообщений диалогов
        movesToTopDialogsLiveData.filter = { current, new ->
            current.recipient.uid == new!!.recipient.uid && new.lastMessage.date >= current.lastMessage.date
        }
    }

    fun start() {
        // Сначала подгрузим начальные диалоги из БД ...
        initJob = GlobalScope.launch {
            if (isActive) {
                var dialogs = dialogsRepository
                        .loadDialogs(onlyFromDatabase = true)
                        .await()

                if (isActive) {
                    if (dialogs != null) {
                        initialDialogsLiveData.postValue(dialogs)
                        dialogsCount = dialogs.size
                    }

                    // Listen updates
                    listenMovesToTop()
                    listenRecipientUpdates()

                    // If session active - load initial dialogs from network
                    if (authRepository.isSessionInstalled) {
                        dialogs = dialogsRepository
                                .loadDialogs(onlyFromNetwork = true)
                                .await()

                        initialDialogsLiveData.postValue(dialogs)
                        dialogsCount = dialogs?.size ?: 0
                    } else {
                        // Dialogs will be loaded/updated when the session is initialized
                    }

                    // Listen updates ...
                    listenAccountSession()
                }
            }
        }
    }

    private fun listenAccountSession() = GlobalScope.launch(Dispatchers.IO) {
        for (state in subscriptionsContainer
                .addSubscription(authRepository
                        .accountSessionStateChannel
                        .openSubscription())) {

            if (!state) break
            if (dialogsCount == 0) {
                val dialogs = dialogsRepository
                        .loadDialogs(onlyFromNetwork = true)
                        .await()

                initialDialogsLiveData.postValue(dialogs)
                dialogsCount = dialogs?.size ?: 0
            } else {
                updateJob = updateDialogs()
            }
        }
    }

    private fun listenMovesToTop() = GlobalScope.launch(Dispatchers.IO) {
        for (message in subscriptionsContainer
                .addSubscription(dialogsRepository
                        .dialogMessageForMovingToTopChannel
                        .openSubscription())) {

            if (dialogsCount == 0) {
                val dialog = dialogsRepository.buildDialogWithLastMessage(message) ?: continue

                // Обновим счетчики
                dialogsCount++
                movesToTopDialogsLiveData.postValue(dialog)
            } else {
                movesToTopMessagesLiveData.postValue(message)
            }
        }
    }

    private fun listenRecipientUpdates() = GlobalScope.launch(Dispatchers.IO) {
        for (user in subscriptionsContainer
                .addSubscription(dialogsRepository
                        .dialogRecipientsUpdatesChannel
                        .openSubscription())) {

            recipientsUpdatesLiveData.postValue(user)
        }
    }

    private fun updateDialogs() = GlobalScope.async(Dispatchers.IO)  {
        // Nothing to update
        if (dialogsCount <= 0) return@async

        // Reset marker
        dialogsEnded = false

        val updatedDialogs = ArrayList<Dialog>()
        val neededRequests = Math
                .ceil(dialogsCount / DialogsRepository.MAX_LIMIT_SIZE.toDouble())
                .toInt()

        for (i in 0 until neededRequests) {
            try {
                val dialogs = dialogsRepository
                        .loadDialogs(offset = i * DialogsRepository.MAX_LIMIT_SIZE)
                        .await()

                if (!isActive) return@async
                if (dialogs == null) {
                    initialDialogsLiveData.postValue(ArrayList())
                    dialogsCount = updatedDialogs.size
                    return@async
                }

                // Add to showing ...
                updatedDialogs.plusAssign(dialogs)
            } catch (e: InternalRequestException) {
                if (e.errorCode == InternalErrors.LIST_ENDED) {
                    dialogsEnded = true
                }
            }
        }

        if (isActive) {
            initialDialogsLiveData.postValue(updatedDialogs)
            dialogsCount = updatedDialogs.size
        }
    }

    private fun loadNextDialogsAsync() = GlobalScope.async(Dispatchers.IO) {
        if (initJob != null && initJob!!.isActive) initJob!!.join()
        if (updateJob != null && updateJob!!.isActive) updateJob!!.join()

        try {
            val dialogs = dialogsRepository
                    .loadDialogs(offset = dialogsCount)
                    .await()

            if (dialogsCount == 0) {
                initialDialogsLiveData.postValue(dialogs)
            } else {
                pagingDialogsLiveData.postValue(dialogs)
            }

            // New dialogs count
            dialogsCount += dialogs?.size ?: 0
        } catch (e: InternalRequestException) {
            if (e.errorCode == InternalErrors.LIST_ENDED) {
                dialogsEnded = true
            }
        }
    }

    fun loadNextDialogs() {
        if ((pagingJob == null || !pagingJob!!.isActive) && !dialogsEnded) {
            pagingJob = loadNextDialogsAsync()
        }
    }

    fun requestNewDialog(message: DialogMessage) = GlobalScope.launch(Dispatchers.IO) {
        val dialog = dialogsRepository.buildDialogWithLastMessage(message) ?: return@launch

        // Обновим счетчики
        dialogsCount++
        movesToTopDialogsLiveData.postValue(dialog)
    }

    override fun onCleared() {
        subscriptionsContainer.unsubscribeAll()
        initJob?.cancel()
        updateJob?.cancel()
        pagingJob?.cancel()
    }
}