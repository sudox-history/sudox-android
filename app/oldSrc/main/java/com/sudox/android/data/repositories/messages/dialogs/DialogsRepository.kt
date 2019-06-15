package com.sudox.android.data.repositories.messages.dialogs

import com.sudox.android.common.helpers.clear
import com.sudox.android.data.database.model.messages.DialogMessage
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.models.messages.dialogs.Dialog
import com.sudox.android.data.repositories.users.AuthRepository
import com.sudox.android.data.repositories.users.UsersRepository
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DialogsRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                            private val dialogsMessagesRepository: DialogsMessagesRepository,
                                            private val usersRepository: UsersRepository,
                                            private val authRepository: AuthRepository) {

    /**
     * 1-й параметр - ID собеседника.
     * 2-й параметр - новое сообщение.
     */
    var dialogMessageForMovingToTopChannel: ConflatedBroadcastChannel<DialogMessage> = ConflatedBroadcastChannel()
    var dialogRecipientsUpdatesChannel: ConflatedBroadcastChannel<List<User>> = ConflatedBroadcastChannel()

    init {
        listenSessionState()
        listenNewMessages()
        listenSentMessages()
    }

    companion object {
        const val LIMIT_SIZE = 20
        const val MAX_LIMIT_SIZE = 20
    }

    private fun listenSessionState() = GlobalScope.launch(Dispatchers.IO) {
        for (state in authRepository.accountSessionStateChannel.openSubscription()) {
            if (!state) {
                dialogMessageForMovingToTopChannel.clear()
                dialogRecipientsUpdatesChannel.clear()
            }
        }
    }

    private fun listenNewMessages() = GlobalScope.launch(Dispatchers.IO) {
        for (message in dialogsMessagesRepository
                .globalNewMessagesChannel
                .openSubscription()) {

            dialogMessageForMovingToTopChannel.send(message)
        }
    }

    private fun listenSentMessages() = GlobalScope.launch(Dispatchers.IO) {
        for (message in dialogsMessagesRepository
                .globalSentMessageChannel
                .openSubscription()) {

            dialogMessageForMovingToTopChannel.send(message)
        }
    }

    @Throws(InternalRequestException::class)
    fun loadDialogs(offset: Int = 0, limit: Int = LIMIT_SIZE, onlyFromNetwork: Boolean = false, onlyFromDatabase: Boolean = false) = GlobalScope.async(Dispatchers.IO) {
        val lastMessages = dialogsMessagesRepository
                .loadLastMessages(offset, limit, onlyFromNetwork, onlyFromDatabase)
                .await()

        if (lastMessages.isEmpty()) {
            if (offset == 0) {
                return@async null
            } else {
                throw InternalRequestException(InternalErrors.LIST_ENDED)
            }
        } else {
            val lastMessagesRecipientsIds = lastMessages.map { it.getRecipientId() }
            val lastMessagesRecipients = usersRepository
                    .loadUsers(lastMessagesRecipientsIds, onlyFromDatabase = onlyFromDatabase)
                    .await()
            val dialogs = ArrayList<Dialog>()

            // Find pair & build dialog
            for (lastMessage in lastMessages) {
                val user = lastMessagesRecipients
                        .filter { it != null }
                        .find { it.uid == lastMessage.getRecipientId() } ?: continue

                dialogs.plusAssign(Dialog(user, lastMessage))
            }

            if (dialogs.isNotEmpty()) {
                return@async dialogs
            } else {
                return@async null
            }
        }
    }

    suspend fun buildDialogWithLastMessage(message: DialogMessage): Dialog? {
        val recipientUser = usersRepository
                .loadUser(message.getRecipientId())
                .await() ?: return null

        return Dialog(recipientUser, message)
    }
}