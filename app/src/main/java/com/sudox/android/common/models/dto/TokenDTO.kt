package com.sudox.android.common.models.dto

import org.json.JSONObject

class TokenDTO : CanErrorDTO() {

    lateinit var token: String

    var id: Int = 0
    var code: Int = 0

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("token", token)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)
        with(jsonObject) {
            id = optInt("id")
            code = optInt("code")
        }
    }

}