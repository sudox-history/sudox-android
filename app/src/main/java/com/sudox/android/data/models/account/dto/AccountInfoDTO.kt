package com.sudox.android.data.models.account.dto

import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class AccountInfoDTO : JsonModel() {

    lateinit var id: String
    lateinit var name: String
    lateinit var nickname: String
    lateinit var email: String
    lateinit var photo: AvatarInfo
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
        photo = AvatarInfo.parse(jsonObject.optString("photo"))
        status = jsonObject.optString("status")
        bio = jsonObject.optString("bio")
    }
}