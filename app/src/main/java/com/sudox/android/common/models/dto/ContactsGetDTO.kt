package com.sudox.android.common.models.dto

import org.json.JSONObject

class ContactsGetDTO : CanErrorDTO() {


    lateinit var id: String

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("id", id)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)

    }
}