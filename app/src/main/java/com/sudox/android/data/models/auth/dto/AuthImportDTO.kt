package com.sudox.android.data.models.auth.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class AuthImportDTO : JsonModel() {

    var id: Long = 0
    lateinit var secret: String

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("bytes", secret)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {}
}