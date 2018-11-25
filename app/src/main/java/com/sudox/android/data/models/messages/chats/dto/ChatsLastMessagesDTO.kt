package com.sudox.android.data.models.messages.chats.dto

import com.sudox.protocol.helpers.forEachObject
import com.sudox.protocol.models.JsonModel
import org.json.JSONArray
import org.json.JSONObject

class ChatsLastMessagesDTO : JsonModel() {

    // For sending
    var limit = 0
    var offset = 0

    // For receiving
    lateinit var messages: ArrayList<ChatMessageDTO>

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("limit", limit)
            putOpt("offset", offset)
        }
    }

    override fun fromJSONArray(jsonArray: JSONArray) {
        messages = arrayListOf()

        // Read data
        jsonArray.forEachObject {
            messages.plusAssign(ChatMessageDTO().apply {
                fromJSON(it)
            })
        }

        // Sort by date (TODO: Sort by id after update server!)
        messages.sortByDescending { it.date }
    }
}