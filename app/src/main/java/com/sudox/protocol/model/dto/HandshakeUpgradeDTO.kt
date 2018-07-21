package com.sudox.protocol.model.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONObject

data class HandshakeUpgradeToServerDTO(val payload: String, val hash: String) : JsonModel {
    override fun toJSON(): JSONObject {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fromJSON(jsonObject: JSONObject) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
data class HandshakeUpgradeFromServerDTO(val result: Int)