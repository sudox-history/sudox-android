package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ContactAddDTO : JsonModel() {

    lateinit var name: String
    lateinit var phone: String

    // For read/send ...
    var id: Long = 0

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            putOpt("name", name)
            putOpt("phone", phone)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optLong("id")
    }
}