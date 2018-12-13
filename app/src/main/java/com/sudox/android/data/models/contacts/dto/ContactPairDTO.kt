package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ContactPairDTO : JsonModel() {

    var phone: String? = null
    var name: String? = null

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("phone", phone)
            put("name", name)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        phone = jsonObject.optString("phone")
        name = jsonObject.optString("name")
    }
}