package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONArray
import org.json.JSONObject

class GetMessagesDTO: JsonModel {


    // To send
    var offset = 0
    var limit = 50
    lateinit var id: String

    // To get
    lateinit var items: JSONArray

    // Common
    var errorCode = -1


    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("offset", offset)
            putOpt("limit", limit)
            putOpt("id", id)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if(jsonObject.has("error")){
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            val response = jsonObject.getJSONArray("response")
            items = response
        }
    }
}