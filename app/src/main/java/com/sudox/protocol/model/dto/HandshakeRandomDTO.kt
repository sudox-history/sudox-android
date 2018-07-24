package com.sudox.protocol.model.dto

import org.json.JSONObject

class HandshakeRandomDTO : JsonModel {

    // Data
    var random: String? = null

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("random", random)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}