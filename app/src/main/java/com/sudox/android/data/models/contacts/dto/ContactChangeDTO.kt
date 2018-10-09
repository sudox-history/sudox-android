package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ContactChangeDTO : JsonModel() {

    // For read ...
    lateinit var id: String

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optString("id")
    }
}