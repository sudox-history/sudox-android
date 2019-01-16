package com.sudox.android.data.models.messages.dialogs.dto

import com.sudox.protocol.helpers.reversedForEachObject
import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class LastDialogsMessagesDTO : JsonModel() {

    // For send
    var limit: Int = 0
    var offset: Int = 0

    // For read
    val messages by lazy { ArrayList<DialogMessageDTO>() }

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("limit", limit)
            put("offset", offset)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        val messagesArray = jsonObject.optJSONArray("messages")

        // Reverse! (sorted by id as default)
        messagesArray.reversedForEachObject {
            messages.plusAssign(DialogMessageDTO().apply {
                fromJSON(it)
            })
        }
    }
}