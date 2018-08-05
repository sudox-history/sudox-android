package com.sudox.android.common.models.dto

import org.json.JSONObject

class AuthHashDTO : CanErrorDTO() {

    lateinit var hash: String

    var code: Int = 0

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("hash", hash)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)
        code = jsonObject.optInt("code")
    }
}