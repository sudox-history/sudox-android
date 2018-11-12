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

    var phone: String? = null
    var status: String? = null
    var bio: String? = null

    override fun toJSON(): JSONObject {
        return JSONObject().apply { putOpt("id", id) }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optString("id")
        name = jsonObject.optString("name")
        nickname = jsonObject.optString("nickname")
        photo = jsonObject.optString("photo")

        if (jsonObject.has("phone"))
            phone = jsonObject.optString("phone")
        if (jsonObject.has("status"))
            status = jsonObject.optString("status")
        if (jsonObject.has("bio"))
            bio = jsonObject.optString("bio")
    }
}