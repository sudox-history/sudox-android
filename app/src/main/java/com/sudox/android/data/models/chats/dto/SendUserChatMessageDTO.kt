package com.sudox.android.data.models.chats.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class SendUserChatMessageDTO : JsonModel() {

    lateinit var peerId: String
    lateinit var message: String

    // For reading
    var messageId: Int = 0
    var date: Long = 0

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("id", peerId)
            put("msg", message)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        messageId = jsonObject.optInt("id")
        date = jsonObject.optLong("date")
    }
}