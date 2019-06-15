package com.sudox.android.data.models.messages.dialogs.dto

import com.sudox.protocol.helpers.asIterable
import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class LastDialogsMessagesDTO : JsonModel() {

    // For send
    var limit: Int = 0
    var offset: Int = 0

    // For read
    lateinit var messages: ArrayList<DialogMessageDTO>

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("limit", limit)
            put("offset", offset)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        messages = jsonObject
                .optJSONArray("messages")
                .asIterable()
                .reversed()  // Reversed forEach because messages was sorted by id as default
                .map { DialogMessageDTO().apply { fromJSON(it as JSONObject) } } as ArrayList<DialogMessageDTO>
    }
}