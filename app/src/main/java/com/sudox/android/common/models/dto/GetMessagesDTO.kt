package com.sudox.android.common.models.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONObject

class GetMessagesDTO : JsonModel {


    // To send
    var offset = 0
    var limit = 50
    lateinit var id: String

    // To get
    val messages by lazy { ArrayList<MessageDTO>() }

    // Common
    var errorCode = -1


    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("offset", offset)
            putOpt("limit", limit)
            putOpt("id", id)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if (jsonObject.has("error")) {
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            val array = jsonObject.getJSONArray("response")
            val length = array.length()

            for (i in 0 until length) {
                messages.plusAssign(MessageDTO().apply {
                    fromJSON(array.getJSONObject(i))
                })
            }
        }
    }
}