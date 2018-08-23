package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

class SignInDTO: JsonModel {

    // To send
    var code: Int = 0

    // To get
    lateinit var token: String
    lateinit var id: String

    // Common
    var errorCode = 0

    override fun toJSON(): JSONObject {
       return with(JSONObject()){
           putOpt("code", code)
       }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if(jsonObject.has("error")){
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            val response = jsonObject.getJSONObject("response")
            token = response.optString("token")
            id = response.optString("id")
        }
    }
}