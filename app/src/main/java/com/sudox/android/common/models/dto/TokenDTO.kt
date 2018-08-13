package com.sudox.android.common.models.dto

import org.json.JSONObject

class TokenDTO : CanErrorDTO() {

    lateinit var token: String

    lateinit var id: String
    var code: Int = 0

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("token", token)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)
        with(jsonObject) {
            id = optString("id")
            code = optInt("code")
        }
    }

}