package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

class SignUpDTO: JsonModel {

    // To send
    lateinit var name: String
    lateinit var nickname: String

    // To get
    lateinit var secret: String
    lateinit var id: String
    var errorCode = -1

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("name", name)
            putOpt("nickname", nickname)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if(jsonObject.has("error")){
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            val response = jsonObject.getJSONObject("response")
            id = response.optString("id")
            secret = response.optString("secret")
        }
    }
}