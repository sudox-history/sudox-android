package com.sudox.android.data.models.account.dto

import com.sudox.android.data.models.avatar.Avatar
import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class AccountInfoDTO : JsonModel() {

    lateinit var id: String
    lateinit var name: String
    lateinit var nickname: String
    lateinit var email: String
    lateinit var photo: Avatar
    var status: String? = null
    var bio: String? = null

    override fun toJSON(): JSONObject {
        return JSONObject()
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optString("id")
        name = jsonObject.optString("name")
        nickname = jsonObject.optString("nickname")
        email = jsonObject.optString("email")
        photo = Avatar.parse(jsonObject.optString("avatar"))
        status = jsonObject.optString("status")
        bio = jsonObject.optString("bio")
    }
}