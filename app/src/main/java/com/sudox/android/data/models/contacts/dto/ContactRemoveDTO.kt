package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ContactRemoveDTO : JsonModel() {

    // For read/send ...
    var id: Long = 0L

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            putOpt("id", id)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optLong("id")
    }
}