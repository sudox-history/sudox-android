package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

class SimpleAnswerDTO : JsonModel {

    // To get
    var errorCode = -1
    var response: Int = 0

    override fun toJSON(): JSONObject {
        return JSONObject()
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if (jsonObject.has("error")) {
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            response = jsonObject.optInt("response")
        }
    }

}