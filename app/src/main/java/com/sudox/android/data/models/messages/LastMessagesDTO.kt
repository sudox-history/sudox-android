package com.sudox.android.data.models.messages

import com.sudox.android.data.models.chats.dto.ChatMessageDTO
import com.sudox.protocol.helpers.forEachObject
import com.sudox.protocol.models.JsonModel
import org.json.JSONArray
import org.json.JSONObject

class LastMessagesDTO : JsonModel() {

    // To send
    var limit = 10
    var offset = 0

    // To read
    lateinit var messages: ArrayList<ChatMessageDTO>

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("limit", limit)
            putOpt("offset", offset)
        }
    }

    override fun fromJSONArray(jsonArray: JSONArray) {
        messages = arrayListOf()

        jsonArray.forEachObject {
            messages.plusAssign(ChatMessageDTO().apply {
                fromJSON(it)
            })
        }
    }
}