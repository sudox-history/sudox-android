package com.sudox.protocol.model

import org.json.JSONObject

data class Payload(val payload: String, val iv: String, val hash: String) : JsonModel {

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            put("payload", payload)
            put("iv", iv)
            put("hash", hash)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {

    }
}