package com.sudox.protocol.model.dto

import org.json.JSONObject

class HandshakeUpgradeToServerDTO : JsonModel {

    // Data
    var payload: String? = null
    var hash: String? = null

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("payload", payload)
            putOpt("hash", hash)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class HandshakeUpgradeFromServerDTO : JsonModel {

    // Data
    var code: Int = 0

    override fun toJSON(): JSONObject {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fromJSON(jsonObject: JSONObject) {
        code = jsonObject.optInt("code")
    }
}