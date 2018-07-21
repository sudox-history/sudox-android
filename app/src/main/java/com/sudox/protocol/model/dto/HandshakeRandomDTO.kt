package com.sudox.protocol.model.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONObject

class HandshakeRandomDTO : JsonModel {

    // Data
    lateinit var random: String

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            put("random", random)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}