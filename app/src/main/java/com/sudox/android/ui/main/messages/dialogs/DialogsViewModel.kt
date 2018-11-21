package com.sudox.android.ui.main.messages.dialogs

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.account.state.AccountSessionState
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.DialogsRepository
import javax.inject.Inject

class DialogsViewModel @Inject constructor(val dialogsRepository: DialogsRepository,
                                           val authRepository: AuthRepository) : ViewModel() {

    var initialDialogsLiveData: MutableLiveData<List<Pair<User, ChatMessage>>> = MutableLiveData()
    var partsOfDialogsLiveData: MutableLiveData<List<Pair<User, ChatMessage>>> = MutableLiveData()

    // For updating dialogs
    private val sessionObserver: Observer<AccountSessionState> = Observer {
        if (it!!.lived) loadInitialDialogs()
    }

    init {
        authRepository.accountSessionLiveData.observeForever(sessionObserver)
    }

    fun loadInitialDialogs() = dialogsRepository.loadInitialDialogsFromDb {
        if (it.isNotEmpty()) initialDialogsLiveData.postValue(it)

        // Try load contacts from server
        dialogsRepository.loadInitialDialogsFromServer {
            if (it.isNotEmpty()) initialDialogsLiveData.postValue(it)
        }
    }

    fun loadPartOfDialog(offset: Int) = dialogsRepository.loadDialogsFromServer(offset) {
        if (it.isNotEmpty()) partsOfDialogsLiveData.postValue(it)
    }

    override fun onCleared() {
        super.onCleared()

        // Remove old observer
        authRepository.accountSessionLiveData.removeObserver(sessionObserver)
    }
}