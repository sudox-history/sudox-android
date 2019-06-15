package com.sudox.android.data.models.auth.dto

import com.sudox.android.data.models.users.dto.UserDTO
import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class AuthSignInDTO : JsonModel() {

    // For sending ...
    lateinit var code: String
    lateinit var hash: String
    lateinit var phone: String

    // For receiving ...
    lateinit var token: String
    lateinit var user: UserDTO

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("code", code.toInt())
            put("hash", hash)
            put("phone", phone)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        token = jsonObject.optString("token")
        user = UserDTO().apply { fromJSON(jsonObject.optJSONObject("user")) }
    }
}