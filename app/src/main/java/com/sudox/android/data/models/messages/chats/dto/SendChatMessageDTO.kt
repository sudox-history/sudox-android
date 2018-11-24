package com.sudox.android.data.models.messages.chats.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class SendChatMessageDTO : JsonModel() {

    lateinit var peerId: String
    lateinit var message: String

    // For reading
    lateinit var id: String
    var date: Long = 0

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("id", peerId)
            put("msg", message)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optString("id")
        date = jsonObject.optLong("date")
    }
}