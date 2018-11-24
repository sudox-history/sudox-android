package com.sudox.android.data.models.messages.chats.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ChatMessageDTO : JsonModel() {

    lateinit var id: String
    lateinit var sender: String
    lateinit var peer: String
    lateinit var message: String
    var date: Long = 0

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optString("id")
        sender = jsonObject.optString("sender")
        peer = jsonObject.optString("peer")
        message = jsonObject.optString("msg")
        date = jsonObject.optLong("date")
    }
}