package com.sudox.android.data.models.auth.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class AuthCheckCodeDTO : JsonModel() {

    lateinit var code: String
    lateinit var hash: String
    lateinit var phone: String

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("code", code.toInt())
            put("hash", hash)
            put("phone", phone)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
       // Ignore
    }
}