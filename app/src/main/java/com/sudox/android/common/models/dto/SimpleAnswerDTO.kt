package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

class SimpleAnswerDTO: JsonModel {

    // To get
    var errorCode = 0

    override fun toJSON(): JSONObject {
        return JSONObject()
    }

    override fun fromJSON(jsonObject: JSONObject) {
        errorCode = if(jsonObject.has("error")){
            val response = jsonObject.getJSONObject("error")
            response.optInt("code")
        } else {
            jsonObject.getInt("response")
        }
    }

}