package com.sudox.android.data.models.chats.dto

import com.sudox.protocol.helpers.forEachObject
import com.sudox.protocol.helpers.reversedForEachObject
import com.sudox.protocol.models.JsonModel
import org.json.JSONArray
import org.json.JSONObject

class ChatHistoryDTO : JsonModel() {

    // For send
    lateinit var id: String
    var limit: Int = 0
    var offset: Int = 0

    // For read
    lateinit var messages: ArrayList<ChatMessageDTO>

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("limit", limit)
            put("offset", offset)
        }
    }

    override fun fromJSONArray(jsonArray: JSONArray) {
        messages = arrayListOf()

        // Reverse! (sorted by id as default)
        jsonArray.reversedForEachObject {
            messages.plusAssign(ChatMessageDTO().apply {
                fromJSON(it)
            })
        }
    }
}