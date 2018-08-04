package com.sudox.android.common.models.dto

import org.json.JSONObject

class AuthSessionDTO : CanErrorDTO() {

    var status: Int = 0
    lateinit var hash: String

    override fun toJSON(): JSONObject {
        throw UnsupportedOperationException()
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)

        // Read data
        status = jsonObject.optInt("status")
        hash = jsonObject.optString("hash")
    }
}