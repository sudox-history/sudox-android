package com.sudox.android.common.models.dto

import org.json.JSONObject

class AuthSessionDTO : CanErrorDTO() {

    var code: Int = 0
    lateinit var hash: String

    override fun toJSON(): JSONObject {
        throw UnsupportedOperationException()
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)

        // Read data
        code = jsonObject.optInt("code")
        hash = jsonObject.optString("hash")
    }
}