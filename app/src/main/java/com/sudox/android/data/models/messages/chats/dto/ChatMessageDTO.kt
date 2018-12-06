package com.sudox.android.data.models.messages.chats.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ChatMessageDTO : JsonModel() {

    var id: Long = 0
    var sender: Long = 0
    var peer: Long = 0
    lateinit var message: String
    var date: Long = 0

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optLong("id")
        sender = jsonObject.optLong("sender")
        peer = jsonObject.optLong("peer")
        message = jsonObject.optString("msg")
        date = jsonObject.optLong("date")
    }
}