package com.sudox.protocol.model

import org.json.JSONObject

data class Payload(val payload: String, val iv: String, val hash: String) : JsonModel {

    override fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        with(jsonObject){
            put("payload", payload)
            put("iv", iv)
            put("hash", hash)
        }
        return jsonObject
    }

    override fun fromJSON(jsonObject: JSONObject) {

    }
}