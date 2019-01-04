package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ContactEditDTO : JsonModel() {

    var id: Long = 0
    lateinit var name: String

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            putOpt("id", id)
            putOpt("name", name)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optLong("id")
    }
}