package com.sudox.android.data.models.auth.dto

import com.sudox.android.data.models.users.dto.UserDTO
import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class AuthSignUpDTO : JsonModel() {

    // For sending ...
    lateinit var phone: String
    lateinit var hash: String
    lateinit var code: String
    lateinit var name: String
    lateinit var nickname: String

    // For receiving ...
    lateinit var token: String
    lateinit var user: UserDTO

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("phone", phone)
            put("hash", hash)
            put("code", code.toInt())
            put("name", name)
            put("nickname", nickname)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        token = jsonObject.optString("token")
        user = UserDTO().apply { fromJSON(jsonObject.optJSONObject("user")) }
    }
}