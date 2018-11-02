package com.sudox.android.data.models.chats.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class SendChatMessageDTO : JsonModel() {

    lateinit var peerId: String
    lateinit var message: String

    // For reading
    lateinit var messageId: String
    var date: Long = 0

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("id", peerId)
            put("msg", message)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        messageId = jsonObject.optString("id")
        date = jsonObject.optLong("date")
    }
}