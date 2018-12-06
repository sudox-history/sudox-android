package com.sudox.android.data.models.messages.chats.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class SendChatMessageDTO : JsonModel() {

    var peerId: Long = 0
    lateinit var message: String

    // For reading
    var id: Long = 0
    var date: Long = 0

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("id", peerId)
            put("msg", message)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optLong("id")
        date = jsonObject.optLong("date")
    }
}