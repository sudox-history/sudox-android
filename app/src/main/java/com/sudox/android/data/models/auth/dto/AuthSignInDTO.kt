package com.sudox.android.data.models.auth.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class AuthSignInDTO : JsonModel() {

    // For sending ...
    lateinit var code: String
    lateinit var hash: String
    lateinit var phoneNumber: String

    // For receiving ...
    var id: Long = 0
    lateinit var secret: String

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("code", code.toInt())
            put("hash", hash)
            put("phone", phoneNumber)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optLong("id")
        secret = jsonObject.optString("bytes")
    }
}