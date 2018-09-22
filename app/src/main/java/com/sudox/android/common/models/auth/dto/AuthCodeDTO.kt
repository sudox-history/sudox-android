package com.sudox.android.common.models.auth.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class AuthCodeDTO : JsonModel() {

    // For sending
    lateinit var email: String

    // For receive
    lateinit var hash: String
    var status: Int = -1

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("email", email)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        hash = jsonObject.optString("hash")
        status = jsonObject.optInt("status")
    }
}