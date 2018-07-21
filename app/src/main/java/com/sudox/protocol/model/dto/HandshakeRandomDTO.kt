package com.sudox.protocol.model.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONObject

data class HandshakeRandomDTO(val random: String) : JsonModel{
    override fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("random", random)
        return json
    }

    override fun fromJSON(jsonObject: JSONObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}