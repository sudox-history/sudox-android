package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ContactAddDTO : JsonModel() {

    // For read ...
    lateinit var name: String
    lateinit var phone: String

    lateinit var id: String
    lateinit var nameS: String
    lateinit var nickname: String
    lateinit var photo: String
    var status: String? = null
    var bio: String? = null

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            putOpt("name", name)
            putOpt("phone", phone)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optString("id")
        nameS = jsonObject.optString("name")
        nickname = jsonObject.optString("nickname")
        photo = jsonObject.optString("photo")

        if(jsonObject.has("status"))
            status = jsonObject.optString("status")
        if(jsonObject.has("bio"))
            bio = jsonObject.optString("bio")
    }
}