package com.sudox.android.data.models.messages.chats.dto

import com.sudox.protocol.helpers.reversedForEachObject
import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ChatHistoryDTO : JsonModel() {

    // For send
    var id: Long = 0
    var limit: Int = 0
    var offset: Int = 0

    // For read
    val messages by lazy { ArrayList<ChatMessageDTO>() }

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("limit", limit)
            put("offset", offset)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        val messagesArray = jsonObject.optJSONArray("messages")

        // Reverse! (sorted by id as default)
        messagesArray.reversedForEachObject {
            messages.plusAssign(ChatMessageDTO().apply {
                fromJSON(it)
            })
        }
    }
}