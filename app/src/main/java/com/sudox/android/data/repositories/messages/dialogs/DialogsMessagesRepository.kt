package com.sudox.android.data.repositories.messages.dialogs

import com.sudox.android.common.helpers.clear
import com.sudox.android.common.helpers.formatMessageText
import com.sudox.android.data.database.dao.messages.DialogMessagesDao
import com.sudox.android.data.database.model.messages.DialogMessage
import com.sudox.android.data.database.model.user.User
import com.sudox.android.data.exceptions.InternalRequestException
import com.sudox.android.data.models.common.Errors
import com.sudox.android.data.models.common.InternalErrors
import com.sudox.android.data.models.messages.MessageDirection
import com.sudox.android.data.models.messages.MessageStatus
import com.sudox.android.data.models.messages.dialogs.dto.DialogHistoryDTO
import com.sudox.android.data.models.messages.dialogs.dto.DialogMessageDTO
import com.sudox.android.data.models.messages.dialogs.dto.DialogSendMessageDTO
import com.sudox.android.data.models.messages.dialogs.dto.LastDialogsMessagesDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.main.UsersRepository
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.NetworkException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DialogsMessagesRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                                    private val authRepository: AuthRepository,
                                                    private val accountRepository: AccountRepository,
                                                    private val usersRepository: UsersRepository,
                                                    private val dialogMessagesDao: DialogMessagesDao) {

    // ID загруженных чатов ...
    private val loadedDialogsRecipientIds = hashMapOf<Long, Int>()
    private val messagesSendingThreadContext = newSingleThreadContext("Sudox Dialogs Messages Sending Queue")

    // PublishSubject для доставки новых сообщений.
    val globalNewMessagesChannel: ConflatedBroadcastChannel<DialogMessage> = ConflatedBroadcastChannel()
    var globalSentMessageChannel: ConflatedBroadcastChannel<DialogMessage> = ConflatedBroadcastChannel()
    var dialogDialogNewMessageChannel: ConflatedBroadcastChannel<DialogMessage>? = null
    var dialogDialogSentMessageChannel: ConflatedBroadcastChannel<DialogMessage>? = null
    var dialogRecipientUpdateChannel: ConflatedBroadcastChannel<User>? = null
    var openedDialogRecipientId: Long = 0

    init {
        listenNewMessages()
        listenSessionStatus()
    }

    private fun listenSessionStatus() = GlobalScope.launch(Dispatchers.IO) {
        for (status in authRepository
                .accountSessionStateChannel
                .openSubscription()) {

            if (status) {
                loadedDialogsRecipientIds.clear() // Clean initial cache.

                // Reload initial copy
                if (openedDialogRecipientId != 0L) {
                    // Грузим юзера для апдейта ...
                    val user = usersRepository
                            .loadUser(openedDialogRecipientId)
                            .await()

                    if (user != null) {
                        dialogRecipientUpdateChannel?.offer(user)
                    }
                }
            } else {
                endDialog()

                // Remove old RAM-cached data
                globalNewMessagesChannel.clear()
                globalSentMessageChannel.clear()
            }
        }
    }

    private fun listenNewMessages() {
        protocolClient.listenMessage<DialogMessageDTO>("updates.dialogs.new") {
            val accountId = accountRepository.cachedAccount?.id ?: return@listenMessage
            val recipientId = if (it.peer == accountId) it.sender else it.peer
            val direction = if (it.peer != accountId) MessageDirection.TO else MessageDirection.FROM
            val message = DialogMessage(
                    mid = it.id,
                    sender = it.sender,
                    peer = it.peer,
                    message = it.message,
                    date = it.date,
                    direction = direction,
                    status = MessageStatus.DELIVERED)

            // Insert into database
            dialogMessagesDao.insertOne(message)

            // Notify listeners about new message
            globalNewMessagesChannel.sendBlocking(message)

            // If current dialog active notify subscribers
            if (openedDialogRecipientId == recipientId) {
                dialogDialogNewMessageChannel?.sendBlocking(message)
            }
        }
    }

    fun openDialog(recipientId: Long) {
        openedDialogRecipientId = recipientId
        dialogDialogSentMessageChannel = ConflatedBroadcastChannel()
        dialogDialogNewMessageChannel = ConflatedBroadcastChannel()
        dialogRecipientUpdateChannel = ConflatedBroadcastChannel()
    }

    fun endDialog() {
        openedDialogRecipientId = 0

        // Close old channel
        if (dialogDialogNewMessageChannel != null && !dialogDialogNewMessageChannel!!.isClosedForSend) {
            dialogDialogNewMessageChannel!!.close()
        }

        if (dialogDialogSentMessageChannel != null && !dialogDialogSentMessageChannel!!.isClosedForSend) {
            dialogDialogSentMessageChannel!!.close()
        }

        if (dialogRecipientUpdateChannel != null && !dialogRecipientUpdateChannel!!.isClosedForSend) {
            dialogRecipientUpdateChannel!!.close()
        }
    }

    fun loadMessages(recipientId: Long, offset: Int, limit: Int, onlyFromNetwork: Boolean = false, excludeDelivering: Boolean = false) = GlobalScope.async(Dispatchers.IO) {
        val cachedOffset = loadedDialogsRecipientIds[recipientId] ?: -1
        val newOffset = if (offset > 0) recalculateNetworkOffset(recipientId, offset) else 0

        if (onlyFromNetwork || (protocolClient.isValid() && authRepository.sessionIsValid && cachedOffset < newOffset)) {
            loadMessagesFromNetwork(recipientId, offset, limit, excludeDelivering)
        } else {
            loadMessagesFromDatabase(recipientId, offset, limit)
        }
    }

    @Throws(InternalRequestException::class)
    private suspend fun loadMessagesFromNetwork(recipientId: Long, offset: Int = 0, limit: Int = 20, excludeDelivering: Boolean): ArrayList<DialogMessage> {
        // Exclude delivering messages from offset
        val newOffset = if (offset > 0) recalculateNetworkOffset(recipientId, offset) else 0

        try {
            // Try to get messages from server
            val dialogHistoryDTO = protocolClient.makeRequestWithControl<DialogHistoryDTO>("dialogs.getHistory", DialogHistoryDTO().apply {
                this.id = recipientId
                this.limit = limit
                this.offset = newOffset
            }).await()

            if (dialogHistoryDTO.isSuccess()) {
                val messages = toStorableMessages(dialogHistoryDTO.messages)

                // Remove old messages if dialog loaded firstly in connection
                if (!loadedDialogsRecipientIds.containsKey(recipientId)) {
                    removeSavedMessages(recipientId)
                }

                /**
                 * More than 1000 messages will never be saved in the dialog,
                 * because we delete old messages when we first request to the network and do not save messages when
                 * the offset is more than 1000 **/
                if (newOffset < 1000) {
                    dialogMessagesDao.updateOrInsertMessages(messages)

                    // Update offset cache (for offline mode supporting)
                    if (offset == 0 && limit > 20) {
                        updateCachedOffset(recipientId, messages.size)
                    } else {
                        updateCachedOffset(recipientId, newOffset)
                    }
                }

                // This is start of a dialog if offset equals 0
                return if (newOffset == 0) {
                    if (excludeDelivering) {
                        ArrayList(messages)
                    } else {
                        dialogMessagesDao.buildInitialCopy(recipientId, messages)
                    }
                } else {
                    ArrayList(messages)
                }
            } else {
                if (newOffset == 0) {
                    removeSavedMessages(recipientId)

                    // newOffset == 0 => first initializing
                    return if (excludeDelivering) {
                        ArrayList()
                    } else {
                        ArrayList(dialogMessagesDao.loadDeliveringMessages(recipientId))
                    }
                } else if (dialogHistoryDTO.error == Errors.INVALID_USER) {
                    throw InternalRequestException(InternalErrors.LIST_ENDED)
                }
            }
        } catch (e: NetworkException) {
            loadMessagesFromDatabase(recipientId, offset, limit)
        }

        return arrayListOf()
    }

    @Throws(InternalRequestException::class)
    private fun loadMessagesFromDatabase(recipientId: Long, offset: Int = 0, limit: Int = 20): ArrayList<DialogMessage> {
        val messages = dialogMessagesDao.loadMessages(recipientId, offset, limit)

        // Remove from cache
        return if (messages.isEmpty()) {
            if (offset == 0) {
                removeSavedMessages(recipientId, false)

                // Return result
                arrayListOf()
            } else {
                throw InternalRequestException(InternalErrors.LIST_ENDED)
            }
        } else {
            ArrayList(messages)
        }
    }

    fun loadLastMessages(offset: Int, limit: Int, onlyFromNetwork: Boolean = false, onlyFromDatabase: Boolean = false) = GlobalScope.async(Dispatchers.IO) {
        if ((onlyFromNetwork || (protocolClient.isValid() && authRepository.sessionIsValid)) && !onlyFromDatabase) {
            loadLastMessagesFromNetwork(offset, limit)
        } else {
            loadLastMessagesFromDatabase(offset, limit)
        }
    }

    private suspend fun loadLastMessagesFromNetwork(offset: Int, limit: Int): ArrayList<DialogMessage> {
        try {
            val lastDialogsMessages = protocolClient.makeRequestWithControl<LastDialogsMessagesDTO>("dialogs.get", LastDialogsMessagesDTO().apply {
                this.offset = offset
                this.limit = limit
            }).await()

            if (lastDialogsMessages.containsError() || lastDialogsMessages.messages.isEmpty()) {
                if ((lastDialogsMessages.error == Errors.EMPTY_DIALOGS || lastDialogsMessages.messages.isEmpty()) && offset == 0) {
                    removeAllSavedMessages() // Invalidate all messages
                }

                // No dialogs, sorry ;(
                return arrayListOf()
            }

            // Save last messages
            dialogMessagesDao.updateOrInsertMessages(toStorableMessages(lastDialogsMessages.messages))
            return loadLastMessagesFromDatabase(offset, limit)
        } catch (e: NetworkException) {
            return loadLastMessagesFromDatabase(offset, limit)
        }
    }

    private fun loadLastMessagesFromDatabase(offset: Int, limit: Int): ArrayList<DialogMessage> {
        return ArrayList(dialogMessagesDao.loadLastMessages(offset, limit))
    }

    @Suppress("NAME_SHADOWING")
    fun sendTextMessage(recipientId: Long, text: String) = GlobalScope.launch(Dispatchers.IO) {
        val accountId = accountRepository.cachedAccount?.id ?: return@launch
        val text = formatMessageText(text)

        // Запрет отправки пустого поля.
        if (text.isEmpty()) return@launch

        // Отправка ...
        sendMessage(DialogMessage(
                sender = accountId,
                peer = recipientId,
                message = text,
                date = System.currentTimeMillis(),
                direction = MessageDirection.TO,
                status = MessageStatus.IN_DELIVERY)).await()
    }

    @Suppress("NAME_SHADOWING")
    private suspend fun sendMessage(message: DialogMessage) = GlobalScope.async(messagesSendingThreadContext) {
        // It's new message
        if (message.lid == 0L) {
            message.lid = dialogMessagesDao.insertOne(message)
        }

        // Change status of message
        message.status = MessageStatus.IN_DELIVERY

        // Notify subscribers, that message status was changed
        notifyMessageStatus(message, message.peer)

        // Sending ...
        try {
            val sendDialogMessageDTO = protocolClient.makeRequestWithControl<DialogSendMessageDTO>("dialogs.send", DialogSendMessageDTO().apply {
                this.peerId = message.peer
                this.message = message.message
            }).await()

            if (sendDialogMessageDTO.isSuccess()) {
                message.mid = sendDialogMessageDTO.id
                message.date = sendDialogMessageDTO.date
                message.status = MessageStatus.DELIVERED
            } else {
                message.status = MessageStatus.NOT_DELIVERED
            }
        } catch (e: NetworkException) {
            message.status = MessageStatus.NOT_DELIVERED
        }

        // Update data ...
        dialogMessagesDao.updateOne(message)

        // Notify subscribers, that message status was changed
        notifyMessageStatus(message, message.peer)
    }

    private suspend fun notifyMessageStatus(message: DialogMessage, recipientId: Long) {
        // Notify subscribers, that message in delivery
        globalSentMessageChannel.send(message)

        // For current dialog
        if (openedDialogRecipientId == recipientId)
            dialogDialogSentMessageChannel?.send(message)
    }

    private fun recalculateNetworkOffset(recipientId: Long, initialOffset: Int): Int {
        return Math.max(initialOffset - dialogMessagesDao.countDeliveringMessages(recipientId), 0)
    }

    private fun updateCachedOffset(recipientId: Long, offset: Int) {
        loadedDialogsRecipientIds[recipientId] = offset // Allow load from this offset
    }

    private fun removeSavedMessages(recipientId: Long, removeFromDb: Boolean = true) {
        loadedDialogsRecipientIds.minusAssign(recipientId)

        // Экономим на запросах к БД.
        if (removeFromDb) dialogMessagesDao.removeAllDeliveredMessages(recipientId)
    }

    private fun removeAllSavedMessages(removeFromDb: Boolean = true) {
        loadedDialogsRecipientIds.clear()

        // Экономим на запросах к БД.
        if (removeFromDb) dialogMessagesDao.removeAllDeliveredMessages()
    }

    private fun toStorableMessages(messages: ArrayList<DialogMessageDTO>): ArrayList<DialogMessage> {
        val accountId = accountRepository.cachedAccount?.id ?: return arrayListOf()

        return ArrayList(messages.map {
            val direction = if (it.peer != accountId) MessageDirection.TO else MessageDirection.FROM

            DialogMessage(
                    mid = it.id,
                    sender = it.sender,
                    peer = it.peer,
                    message = it.message,
                    date = it.date,
                    direction = direction,
                    status = MessageStatus.DELIVERED)
        })
    }
}