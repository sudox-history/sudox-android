package com.sudox.android.data.repositories.main

import com.sudox.android.data.database.dao.ChatMessagesDao
import com.sudox.android.data.database.dao.UserDao
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.messages.LastMessagesDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
import com.sudox.android.data.repositories.messages.MESSAGE_FROM
import com.sudox.android.data.repositories.messages.MESSAGE_TO
import com.sudox.protocol.ProtocolClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject

const val MAX_INITIAL_DIALOGS_COUNT = 10

class DialogsRepository @Inject constructor(val protocolClient: ProtocolClient,
                                            private val authRepository: AuthRepository,
                                            private val accountRepository: AccountRepository,
                                            private val usersRepository: UsersRepository,
                                            private val messagesDao: ChatMessagesDao,
                                            private val userDao: UserDao) {

    fun loadInitialDialogsFromDb(callback: (List<Pair<User, ChatMessage>>) -> (Unit)) = GlobalScope.async {
        val messages = ArrayList(messagesDao.loadLastMessages(MAX_INITIAL_DIALOGS_COUNT))
        val usersIds = ArrayList<String>()

        // Get users ids
        messages.forEach {
            if (!usersIds.contains(it.peer)) {
                usersIds.plusAssign(it.peer)
            } else if (!usersIds.contains(it.sender)) {
                usersIds.plusAssign(it.sender)
            }
        }

        val users = userDao.getUsers(usersIds)

        // Return result
        callback(buildDialogs(messages, users))
    }

    private fun buildDialogs(messages: List<ChatMessage>, users: List<User>): ArrayList<Pair<User, ChatMessage>> {
        val dialogs: ArrayList<Pair<User, ChatMessage>> = ArrayList()
        val accountId = accountRepository.cachedAccount?.id

        messages.forEach { message ->
            var peer: User? = null
            var sender: User? = null

            for (user in users) {
                if (message.peer == message.sender && (user.uid == message.peer || user.uid == message.sender)) {
                    peer = user
                    sender = user
                    break
                }

                if (user.uid == message.peer) peer = user
                if (user.uid == message.sender) sender = user
                if (peer != null && sender != null) break
            }

            if (peer != null && message.peer != accountId) {
                dialogs.plusAssign(Pair(peer, message))
            } else if (sender != null && message.sender != accountId) {
                dialogs.plusAssign(Pair(sender, message))
            }
        }

        return dialogs
    }


    fun loadInitialDialogsFromServer(callback: (List<Pair<User, ChatMessage>>) -> (Unit)) = GlobalScope.async {
        if (!protocolClient.isValid()) return@async

        protocolClient.makeRequest<LastMessagesDTO>("chats.getChats", LastMessagesDTO().apply {
            limit = MAX_INITIAL_DIALOGS_COUNT
        }) {
            if (it.containsError()) return@makeRequest

            // Last messages
            val messages = it.messages.map {
                ChatMessage(it.id, it.sender, it.peer, it.message, it.date, if (it.peer == accountRepository.cachedAccount!!.id) {
                    MESSAGE_FROM
                } else {
                    MESSAGE_TO
                })
            }

            // Users ids for users.getUsers
            val usersIds = ArrayList<String>()

            // Search the users ids
            messages.forEach {
                if (!usersIds.contains(it.peer)) {
                    usersIds.plusAssign(it.peer)
                } else if (!usersIds.contains(it.sender)) {
                    usersIds.plusAssign(it.sender)
                }
            }

            // Final step
            usersRepository.getUsers(usersIds) { callback(buildDialogs(messages, it)) }
        }
    }
}