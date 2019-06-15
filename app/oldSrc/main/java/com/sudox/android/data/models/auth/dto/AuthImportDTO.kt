package com.sudox.android.data.models.auth.dto

import com.sudox.android.data.models.users.dto.UserDTO
import com.sudox.protocol.models.JsonModel
import org.json.JSONObject
import java.math.BigInteger

class AuthImportDTO : JsonModel() {

    lateinit var token: String
    lateinit var user: UserDTO

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("token", token)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        user = UserDTO().apply { fromJSON(jsonObject.optJSONObject("user")) }
    }
}