package com.sudox.android.data.repositories.main

import com.sudox.android.data.database.dao.ChatMessagesDao
import com.sudox.android.data.database.dao.UserDao
import com.sudox.android.data.database.model.ChatMessage
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.messages.LastMessagesDTO
import com.sudox.android.data.repositories.auth.AccountRepository
import com.sudox.android.data.repositories.auth.AuthRepository
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
        val accountId = accountRepository.cachedAccount?.id
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
        val dialogs = arrayListOf<Pair<User, ChatMessage>>()

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

            if (peer != null && sender != null) {
                val user = if (peer.uid == accountId) sender else peer

                // Build dialog
                dialogs.plusAssign(Pair(user, message))
            }
        }

        // Return result
        callback(dialogs)
    }


    fun loadInitialDialogsFromServer(callback: (List<Pair<User, ChatMessage>>) -> (Unit)) {
        if(protocolClient.isValid()){
            protocolClient.makeRequest<LastMessagesDTO>("chats.getChats", LastMessagesDTO().apply {
                limit = MAX_INITIAL_DIALOGS_COUNT
                offset = 0
            }) {



            }
        }
    }
}