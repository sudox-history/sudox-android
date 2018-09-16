package com.sudox.android.common.models.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONObject

class SendCodeDTO : JsonModel {

    // To send
    lateinit var email: String

    // To get
    lateinit var hash: String
    var status = 0
    var errorCode = -1

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("email", email)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if(jsonObject.has("error")){
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            val response = jsonObject.getJSONObject("response")
            hash = response.optString("hash")
            status = response.optInt("status")
        }
    }
}