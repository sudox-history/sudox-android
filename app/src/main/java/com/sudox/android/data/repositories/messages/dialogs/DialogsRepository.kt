package com.sudox.android.data.repositories.messages.dialogs

import com.sudox.android.data.database.model.messages.DialogMessage
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.models.messages.dialogs.Dialog
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.UsersRepository
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
    var dialogRecipientUpdateChannel: ConflatedBroadcastChannel<User> = ConflatedBroadcastChannel()

    init {
        listenNewMessages()
        listenSentMessages()
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
    fun loadDialogs(offset: Int = 0, limit: Int = 20, onlyFromNetwork: Boolean = false) = GlobalScope.async(Dispatchers.IO) {
        val lastMessages = dialogsMessagesRepository
                .loadLastMessages(offset, limit, onlyFromNetwork)
                .await()

        if (lastMessages.isEmpty()) {
            if (offset == 0) {
                return@async arrayListOf<Dialog>()
            } else {
                throw InternalRequestException(InternalErrors.LIST_ENDED)
            }
        } else {
            val lastMessagesRecipientsIds = lastMessages.map { it.getRecipientId() }
            val lastMessagesRecipients = usersRepository
                    .loadUsers(lastMessagesRecipientsIds)
                    .await()
            val dialogs = ArrayList<Dialog>()

            // Find pair & build dialog
            for (lastMessage in lastMessages) {
                val user = lastMessagesRecipients
                        .filter { it != null }
                        .find { it.uid == lastMessage.getRecipientId() } ?: continue

                dialogs.plusAssign(Dialog(user, lastMessage))
            }

            return@async dialogs
        }
    }

    suspend fun buildDialogWithLastMessage(message: DialogMessage): Dialog? {
        val recipientUser = usersRepository
                .loadUser(message.getRecipientId())
                .await() ?: return null

        return Dialog(recipientUser, message)
    }
}