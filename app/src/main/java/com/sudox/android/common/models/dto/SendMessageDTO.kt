package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

class SendMessageDTO: JsonModel {

    // To send
    lateinit var sendId: String
    lateinit var text: String

    // To get
    var time: Long = -1
    lateinit var id: String
    lateinit var toId: String

    // Common
    var errorCode = -1

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("id",sendId)
            putOpt("text",text)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if(jsonObject.has("error")){
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            val response = jsonObject.getJSONObject("response")
            time = response.optLong("date")
            id = response.optString("id")
            toId = response.optString("to_id")
            text = response.optString("text")
        }
    }
}