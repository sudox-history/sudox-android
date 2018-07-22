package com.sudox.protocol.model

import org.json.JSONObject

class Payload : JsonModel {

    var payload: String? = null
    var iv: String? = null
    var hash: String? = null

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("payload", payload)
            putOpt("iv", iv)
            putOpt("hash", hash)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}