package com.sudox.android.data.models.auth.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class AuthSignUpDTO : JsonModel() {

    // For sending ...
    lateinit var phoneNumber: String
    lateinit var hash: String
    lateinit var code: String
    lateinit var name: String
    lateinit var nickname: String

    // For receiving ...
    var id: Long = 0
    lateinit var secret: String

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("phone", phoneNumber)
            put("hash", hash)
            put("code", code.toInt())
            put("name", name)
            put("nickname", nickname)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optLong("id")
        secret = jsonObject.optString("bytes")
    }
}