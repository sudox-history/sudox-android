package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

class ContactAddRemoveDTO: JsonModel {

    // To send
    var sendId: String? = null

    // To get
    lateinit var id: String
    var code: Int = 0

    // Common
    var errorCode = 0

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("id", sendId)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if(jsonObject.has("error")){
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            id = jsonObject.optString("response")
        }
    }
}