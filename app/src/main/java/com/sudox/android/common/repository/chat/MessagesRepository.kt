package com.sudox.android.common.repository.chat

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sudox.android.common.enums.SendMessageState
import com.sudox.android.common.enums.State
import com.sudox.android.common.models.NewMessageData
import com.sudox.android.common.models.SendMessageData
import com.sudox.android.common.models.dto.GetMessagesDTO
import com.sudox.android.common.models.dto.MessageDTO
import com.sudox.android.common.models.dto.NotificationDTO
import com.sudox.android.common.models.dto.SendMessageDTO
import com.sudox.android.database.dao.MessagesDao
import com.sudox.android.database.model.Message
import com.sudox.protocol.ProtocolClient


const val MESSAGE_TO = 0
const val MESSAGE_FROM = 1

class MessagesRepository(private val protocolClient: ProtocolClient,
                         private val messagesDao: MessagesDao) {

    var newMessageLiveData = MutableLiveData<NewMessageData?>()

    fun nullLiveData(){
        newMessageLiveData.postValue(null)
    }

    fun initMessagesListeners() {
        protocolClient.listenMessage<NotificationDTO>("notification") {
            if(it.method == "chats.new") {
                val message = Message(it.mid, it.text, it.time, MESSAGE_FROM, it.fromId)
                newMessageLiveData.postValue(NewMessageData(message, it.fromId))
            }
        }
    }

    fun getFirstMessagesFromServer(id: String): LiveData<State> {
        val mutableLiveData = MutableLiveData<State>()

        val getMessagesDTO = GetMessagesDTO()
        getMessagesDTO.id = id

        protocolClient.makeRequest<GetMessagesDTO>("chats.getHistory", getMessagesDTO){
            for (i in 0..(it.items.length() - 1)) {
                val item = it.items.getJSONObject(i)

                val messageDTO = MessageDTO()
                messageDTO.fromJSON(item)

                if(messageDTO.toId == id) {
                    messagesDao.insertMessage(Message(messageDTO.mid, messageDTO.text, messageDTO.time, MESSAGE_TO, messageDTO.toId))
                } else {
                    messagesDao.insertMessage(Message(messageDTO.mid, messageDTO.text, messageDTO.time, MESSAGE_FROM, messageDTO.fromId))
                }
            }
            mutableLiveData.postValue(State.SUCCESS)
        }

        return mutableLiveData
    }

    fun requestFromDB(id: String): LiveData<List<Message>> {
        val mutableLiveData = MutableLiveData<List<Message>>()
        AsyncTask.execute{
            val messages = messagesDao.getMessages(id)
            mutableLiveData.postValue(messages)
        }
        return mutableLiveData
    }

    fun sendSimpleMessage(id: String, text: String): LiveData<SendMessageData> {
        val sendMessageLiveData = MutableLiveData<SendMessageData>()

        val sendMessageDTO = SendMessageDTO()
        sendMessageDTO.sendId = id
        sendMessageDTO.text = text

        protocolClient.makeRequest<SendMessageDTO>("chats.send", sendMessageDTO) {
            if(it.errorCode != 50){
                sendMessageLiveData.postValue(SendMessageData(SendMessageState.SUCCESS, Message(it.id, it.text, it.time, 0, it.toId)))
            } else {
                sendMessageLiveData.postValue(SendMessageData(SendMessageState.FAILED))
            }
        }

        return sendMessageLiveData
    }
}