package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ContactInfoDTO : JsonModel() {

    // For reading
    lateinit var id: String
    lateinit var name: String
    lateinit var nickname: String
    lateinit var photo: String

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optString("id")
        name = jsonObject.optString("name")
        nickname = jsonObject.optString("nickname")
        photo = jsonObject.optString("photo")
    }
}