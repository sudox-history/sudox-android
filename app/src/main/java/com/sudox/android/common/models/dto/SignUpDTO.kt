package com.sudox.android.common.models.dto

import org.json.JSONObject

class SignUpDTO : CanErrorDTO() {

    lateinit var name: String
    lateinit var surname: String

    lateinit var token: String
    var id: Long = 0

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("name", name)
            putOpt("surname", surname)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)
        token = jsonObject.getString("token")
        id = jsonObject.getLong("id")
    }
}