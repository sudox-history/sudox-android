package com.sudox.android.ui.datasource

import androidx.paging.PositionalDataSource
import com.sudox.android.common.repository.chat.MessagesRepository
import com.sudox.android.database.model.Message
import com.sudox.protocol.ProtocolClient
import javax.inject.Inject

class MessagesDataSource @Inject constructor(val protocolClient: ProtocolClient,
                                             val messagesRepository: MessagesRepository) : PositionalDataSource<Message>() {

    lateinit var userId: String

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Message>) {
        messagesRepository.getFirstMessagesFromServer(userId, params.startPosition, params.loadSize) {
            callback.onResult(it)
        }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Message>) {
        messagesRepository.getFirstMessagesFromServer(userId, params.requestedStartPosition, params.requestedLoadSize) {
            callback.onResult(it, 0)
        }
    }
}