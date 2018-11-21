package com.sudox.android.ui.main.messages.dialogs

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.account.state.AccountSessionState
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.DialogsRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import javax.inject.Inject

class DialogsViewModel @Inject constructor(val dialogsRepository: DialogsRepository,
                                           val authRepository: AuthRepository,
                                           val protocolClient: ProtocolClient) : ViewModel() {

    var initialDialogsLiveData: MutableLiveData<List<Pair<User, ChatMessage>>> = SingleLiveEvent()
    var partsOfDialogsLiveData: MutableLiveData<List<Pair<User, ChatMessage>>> = SingleLiveEvent()
    var loadedFromNetwork: Boolean = false

    // For updating dialogs
    private val sessionObserver: Observer<AccountSessionState> = Observer {
        if (it!!.lived) loadInitialDialogsFromServer()
    }

    init {
        authRepository.accountSessionLiveData.observeForever(sessionObserver)
    }

    fun loadInitialDialogsFromDb() = dialogsRepository.loadInitialDialogsFromDb {
        if (it.isNotEmpty()) {
            loadedFromNetwork = false
            initialDialogsLiveData.postValue(it)
        }
    }

    fun loadInitialDialogsFromServer() {
        dialogsRepository.loadInitialDialogsFromServer {
            if (it.isNotEmpty()) {
                loadedFromNetwork = true
                initialDialogsLiveData.postValue(it)
            }
        }
    }

    fun loadPartOfDialog(offset: Int) {
        if (loadedFromNetwork) {
            dialogsRepository.loadDialogsFromServer(offset) {
                if (!loadedFromNetwork) return@loadDialogsFromServer
                if (it.isNotEmpty()) partsOfDialogsLiveData.postValue(it)
            }
        } else {
            dialogsRepository.loadDialogsFromDatabase(offset) {
                if (loadedFromNetwork) return@loadDialogsFromDatabase
                if (it.isNotEmpty()) partsOfDialogsLiveData.postValue(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        // Remove old observer
        authRepository.accountSessionLiveData.removeObserver(sessionObserver)
    }
}