package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ContactEditDTO : JsonModel() {

    var id: Long = 0L
    lateinit var name: String

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            putOpt("id", id)

            if (name.isNotEmpty()) {
                putOpt("name", name)
            }
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optLong("id")
    }
}