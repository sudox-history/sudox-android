package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ContactRemoveDTO: JsonModel() {

    //TO SEND
    lateinit var id: String

    lateinit var name: String

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            putOpt("id", id)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        name = jsonObject.optString("name")
    }
}