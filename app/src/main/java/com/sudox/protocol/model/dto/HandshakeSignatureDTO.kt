package com.sudox.protocol.model.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONObject

class HandshakeSignatureDTO : JsonModel {

    // Data
    var signature: String? = null

    override fun toJSON(): JSONObject {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fromJSON(jsonObject: JSONObject) {
        signature = jsonObject.optString("signature")
    }
}