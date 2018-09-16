package com.sudox.android.common.models.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONObject

class SignInDTO : JsonModel {

    // To send
    var code: Int = 0

    // To get
    var secret: String? = null
    var id: String? = null

    // Common
    var errorCode = -1

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("code", code)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if (jsonObject.has("error")) {
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            val response = jsonObject.getJSONObject("response")
            secret = response.optString("secret")
            id = response.optString("id")
        }
    }
}