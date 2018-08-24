package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

class ConfirmCodeDTO: JsonModel {

    // To send
    var code: Int = 0

    // To get
    var codeStatus: Int = 0
    var errorCode = -1

    // Common
    var isError = false

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
            codeStatus = jsonObject.getInt("response")
        }
    }
}