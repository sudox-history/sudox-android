package com.sudox.android.data.repositories.messages.chats

import com.sudox.android.data.database.dao.messages.ChatMessagesDao
import com.sudox.android.data.database.model.messages.ChatMessage
import com.sudox.android.data.models.Errors
import com.sudox.android.data.models.LoadingType
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.chats.Dialog
import com.sudox.android.data.models.messages.chats.dto.ChatsLastMessagesDTO
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.UsersRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DialogsRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                            private val chatMessagesRepository: ChatMessagesRepository,
                                            private val usersRepository: UsersRepository,
                                            private val authRepository: AuthRepository,
                                            private val chatMessagesDao: ChatMessagesDao) {

    // Initial & paging copies of chats
    var dialogsChannel: BroadcastChannel<Pair<LoadingType, List<Dialog>>> = ConflatedBroadcastChannel()
    var dialogsUpdatesChannel: BroadcastChannel<Dialog> = ConflatedBroadcastChannel()

    // Dialogs loaded from server (if value negative - last messages not loaded)
    private var lastMessagesLoadedOffset: Int = -1
    private var lastMessagesEnded: Boolean = false
    private var isWorking: Boolean = false

    /**
     * Запускает слушателей событий ...
     */
    fun startWork() {
        if (!isWorking) listenConnectionStatus()

        // Защита от двух и более запусков слушателей
        isWorking = true
    }

    private fun listenConnectionStatus() = authRepository.accountSessionLiveData.observeForever {
        if (!it!!.lived) return@observeForever

        resetLoadedOffset()
        loadInitialDialogs()
    }

    fun loadInitialDialogs() = GlobalScope.launch {
        if (lastMessagesLoadedOffset < 0 && protocolClient.isValid()) {
            loadDialogsFromDatabase()
            loadDialogsFromNetwork()
        } else {
            loadDialogsFromDatabase()
        }
    }

    fun loadPagedDialogs(offset: Int) {
        if (offset <= 0) return

        // Initial copy loaded from database
        if (!protocolClient.isValid() || lastMessagesLoadedOffset >= offset) {
            loadDialogsFromDatabase(offset)
        } else if (!lastMessagesEnded) {
            loadDialogsFromNetwork(offset)
        }
    }

    private fun loadDialogsFromNetwork(offset: Int = 0) {
        protocolClient.makeRequest<ChatsLastMessagesDTO>("chats.getChats", ChatsLastMessagesDTO().apply {
            this.limit = 10
            this.offset = offset
        }) {
            if (it.containsError()) {
                if (offset == 0) {
                    resetLoadedOffset()
                    chatMessagesRepository.removeAllSavedMessages()
                    dialogsChannel.sendBlocking(Pair(LoadingType.INITIAL, arrayListOf()))
                } else if (it.error == Errors.EMPTY_CHATS) {
                    lastMessagesEnded = true
                }
            } else {
                val messages = chatMessagesRepository.toStorableMessages(it.messages)
                val dialogs = toDialogs(messages)

                // Cache ...
                chatMessagesDao.insertAll(messages)
                lastMessagesLoadedOffset = offset

                // offset == 0 => first initializing
                if (offset == 0) {
                    dialogsChannel.sendBlocking(Pair(LoadingType.INITIAL, dialogs))
                } else if (dialogs.isNotEmpty()) {
                    dialogsChannel.sendBlocking(Pair(LoadingType.PAGING, dialogs))
                }
            }
        }
    }

    private fun loadDialogsFromDatabase(offset: Int = 0) {
        val messages = chatMessagesDao.loadAll(offset, 10)

        // Сообщения могут отсутствовать в БД
        if (messages.isEmpty()) {
            if (offset == 0) {
                resetLoadedOffset()

                // offset == 0 => first initializing
                dialogsChannel.sendBlocking(Pair(LoadingType.INITIAL, arrayListOf()))
            }
        } else {
            val dialogs = toDialogs(messages)

            // offset == 0 => first initializing
            if (offset == 0) {
                dialogsChannel.sendBlocking(Pair(LoadingType.INITIAL, dialogs))
            } else if (dialogs.isNotEmpty()) {
                dialogsChannel.sendBlocking(Pair(LoadingType.PAGING, dialogs))
            }
        }
    }

    /**
     * Ищет собеседника к сообщению
     * Если собеседник не будет найден, то сообщение не будет отражено в результатах вызова данной функции.
     */
    private fun toDialogs(messages: List<ChatMessage>) = runBlocking {
        val userIdsForLoading = messages.map { if (it.type == MessageDirection.TO) it.peer else it.sender }
        val users = usersRepository.loadUsers(userIdsForLoading).await()
        val dialogs = arrayListOf<Dialog>()

        // Mapping
        messages.forEach {
            val recipientId = if (it.type == MessageDirection.TO) it.peer else it.sender
            val recipient = users.find { it.uid == recipientId }

            // Recipient was loaded
            if (recipient != null) dialogs.plusAssign(Dialog(recipient, it))
        }

        return@runBlocking dialogs
    }

    private fun resetLoadedOffset() {
        lastMessagesEnded = false
        lastMessagesLoadedOffset = -1
    }
}