package com.sudox.android.common.models.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONObject

class NotificationDTO : JsonModel {

    // To get
    lateinit var mid: String
    lateinit var method: String
    lateinit var fromId: String
    lateinit var text: String
    var time: Long = -1

    override fun toJSON(): JSONObject {
        TODO("not implemented")
    }

    override fun fromJSON(jsonObject: JSONObject) {
        val data = jsonObject.getJSONObject("data")
        mid = data.optString("id")
        method = jsonObject.optString("name")
        fromId = data.optString("from_id")
        text = data.optString("text")
        time = data.optLong("date")
    }
}