package com.sudox.android.common.models.dto

import org.json.JSONObject

class SignInDTO : CanErrorDTO() {

    var code: Int = 0

    lateinit var token: String
    var id: Long = 0
    var status: Int = 0

    override fun toJSON(): JSONObject {
       return with(JSONObject()){
           putOpt("code", code)
       }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)
        token = jsonObject.optString("token")
        id = jsonObject.optLong("id")
        status = jsonObject.optInt("code")
    }

}