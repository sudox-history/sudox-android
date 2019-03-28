package com.sudox.protocol.models.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class CoreVersionDTO : JsonModel() {

    lateinit var version: String

    override fun fromJSON(jsonObject: JSONObject) {
        version = jsonObject.optString("version")
    }
}