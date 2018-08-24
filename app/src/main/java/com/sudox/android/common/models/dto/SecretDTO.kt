package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

class SecretDTO: JsonModel {

    // To send
    lateinit var secret: String
    lateinit var sendId: String

    // To get
    var status = 0
    var errorCode: Int = -1

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("secret", secret)
            putOpt("id", sendId)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if(jsonObject.has("error")){
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            status = jsonObject.optInt("response")
        }
    }

}