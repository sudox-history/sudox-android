package com.sudox.android.common.models.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONObject

class AuthHashDTO : JsonModel {

    // To send
    lateinit var hash: String

    // To get
    var code: Int = 0

    // Common
    var errorCode = -1

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("hash", hash)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if (jsonObject.has("error")) {
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            code = jsonObject.getInt("response")
        }
    }
}