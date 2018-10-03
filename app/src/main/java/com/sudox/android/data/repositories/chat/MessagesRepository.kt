package com.sudox.android.data.repositories.chat

import com.sudox.android.data.database.dao.MessagesDao
import com.sudox.android.data.database.model.Message
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.SingleLiveEvent
import kotlinx.coroutines.experimental.async
import javax.inject.Inject
import javax.inject.Singleton

const val MESSAGE_TO = 0
const val MESSAGE_FROM = 1

@Singleton
class MessagesRepository @Inject constructor(private val protocolClient: ProtocolClient,
                                             private val messagesDao: MessagesDao) {

    var canUpdateLiveData: Boolean = false
    val loadedContactsIds: HashSet<String> = HashSet()
    val messagesLiveData = SingleLiveEvent<ArrayList<Message>>()

    fun initMessagesListener() {
//        protocolClient.listenMessage<NotificationDTO>("notification") {
//            if (it.method == "chats.new") {
//                Message(it.mid, it.text, it.time, MESSAGE_FROM, it.fromId).apply {
//                    messagesDao.insertMessage(this)
//                    if (canUpdateLiveData) messagesLiveData.postValue(arrayListOf(this))
//                }
//            }
//        }
//
//        protocolClient.listenMessage<SendMessageDTO>("chats.send") {
//            Message(it.id, it.text, it.time, MESSAGE_TO, it.toId).apply {
//                messagesDao.insertMessage(this)
//                if (canUpdateLiveData) messagesLiveData.postValue(arrayListOf(this))
//            }
//        }
//
//        protocolClient.connectionStateLiveData.observeForever {
//            if (it == ConnectionState.CORRECT_TOKEN) {
//                loadedContactsIds.clear()
//            }
//        }
    }

    fun loadHistoryIntoDatabase(contactId: String, offset: Int, limit: Int) {
//        protocolClient.makeRequest<GetMessagesDTO>("chats.getHistory", GetMessagesDTO().apply {
//            this.id = contactId
//            this.offset = offset
//            this.limit = limit
//        }) {
//            val messages = it.messages.map {
//                Message(it.mid, it.text, it.time,
//                        if (it.toId == contactId) MESSAGE_TO else MESSAGE_FROM,
//                        contactId)
//            }.reversed()
//
//            if (!messages.isEmpty()) {
//                messagesDao.insertAll(messages)
//            }
//
//            loadedContactsIds.plusAssign(contactId)
//            loadHistoryFromDatabase(contactId)
//        }
    }

    fun loadHistoryFromDatabase(contactId: String) = async {
        messagesLiveData.postValue(messagesDao.getMessages(contactId) as ArrayList<Message>)
    }

    fun sendTextMessage(contactId: String, text: String) {
//        protocolClient.sendMessage("chats.send", SendMessageDTO().apply {
//            this.sendId = contactId
//            this.text = text
//        })
    }

//
//    fun initMessagesListeners() {
//        protocolClient.listenMessage<NotificationDTO>("notification") {
//            if (it.method == "chats.new") {
//                val message = Message(it.mid, it.text, it.time, MESSAGE_FROM, it.fromId)
//                messagesDao.insertMessage(message)
//                newMessageLiveData.postValue(NewMessageData(message, it.fromId))
//            }
//        }
//    }

//    fun getFirstMessagesFromServer(id: String) {
//        val getMessagesDTO = GetMessagesDTO()
//        getMessagesDTO.id = id
//
//        protocolClient.makeRequest<GetMessagesDTO>("chats.getHistory", getMessagesDTO) {
//            for (i in 0..(it.items.length() - 1)) {
//                val item = it.items.getJSONObject(i)
//
//                val it = MessageDTO()
//                it.fromJSON(item)
//
//                if (it.toId == id) {
//                    messagesDao.insertMessage(Message(it.mid, it.text, it.time, MESSAGE_TO, it.toId))
//                } else {
//                    messagesDao.insertMessage(Message(it.mid, it.text, it.time, MESSAGE_FROM, it.fromId))
//                }
//            }
//
//            requestFromDB(id)
//        }
//    }
//
//    fun requestFromDB(id: String) {
//        AsyncTask.execute {
//            val messages = messagesDao.getMessages(id)
//            messagesLiveData.postValue(messages as ArrayList<Message>)
//        }
//    }
//
//    fun sendSimpleMessage(id: String, text: String): LiveData<SendMessageData> {
//        val sendMessageLiveData = MutableLiveData<SendMessageData>()
//
//        val sendMessageDTO = SendMessageDTO()
//        sendMessageDTO.sendId = id
//        sendMessageDTO.text = text
//
//        protocolClient.makeRequest<SendMessageDTO>("chats.send", sendMessageDTO) {
//            if (it.errorCode != 50) {
//                val message = Message(it.id, text, it.time, 0, id)
//                messagesDao.insertMessage(message)
//                sendMessageLiveData.postValue(SendMessageData(SendMessageState.SUCCESS, message))
//            } else {
//                sendMessageLiveData.postValue(SendMessageData(SendMessageState.FAILED))
//            }
//        }
//
//        return sendMessageLiveData
//    }
}