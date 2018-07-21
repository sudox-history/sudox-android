package com.sudox.protocol.model.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONObject

class HandshakeUpgradeToServerDTO : JsonModel {

    // Data
    lateinit var payload: String
    lateinit var hash: String

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            put("payload", payload)
            put("hash", hash)
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
        code = jsonObject.getInt("code")
    }
}