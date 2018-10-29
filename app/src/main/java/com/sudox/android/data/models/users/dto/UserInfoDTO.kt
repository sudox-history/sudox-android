package com.sudox.android.data.models.users.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class UserInfoDTO : JsonModel() {

    // For sending & reading
    lateinit var id: String

    // For reading
    lateinit var name: String
    lateinit var nickname: String
    lateinit var photo: String

    override fun toJSON(): JSONObject {
        return JSONObject().apply { putOpt("id", id) }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optString("id")
        name = jsonObject.optString("name")
        nickname = jsonObject.optString("nickname")
        photo = jsonObject.optString("avatar")
    }
}