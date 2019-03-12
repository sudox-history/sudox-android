package com.sudox.android.data.models.users.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class UserDTO : JsonModel() {

    // For reading
    var id: Long = 0
    lateinit var name: String
    lateinit var nickname: String
    lateinit var photo: String
    var phone: String? = null
    var status: String? = null
    var bio: String? = null

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optLong("id")
        name = jsonObject.optString("name")
        nickname = jsonObject.optString("nickname")
        photo = jsonObject.optString("photo")
        phone = jsonObject.optString("phone", null)
        status = jsonObject.optString("status", null)
        bio = jsonObject.optString("bio", null)
    }
}