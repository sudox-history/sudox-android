package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

class MessageDTO: JsonModel {

    //To get
    lateinit var mid: String
    lateinit var toId: String
    lateinit var fromId: String
    lateinit var text: String
    var time: Long = -1

    override fun toJSON(): JSONObject {
        TODO("not implemented")
    }

    override fun fromJSON(jsonObject: JSONObject) {
        mid = jsonObject.optString("id")
        toId = jsonObject.optString("to_id")
        fromId = jsonObject.optString("from_id")
        text = jsonObject.optString("text")
        time = jsonObject.optLong("date")
    }
}