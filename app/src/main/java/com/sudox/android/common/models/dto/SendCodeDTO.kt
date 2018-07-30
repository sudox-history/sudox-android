package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

class SendCodeDTO(val email: String) : JsonModel {

    override fun toJSON(): JSONObject {
        val jsonObject = JSONObject()

        // Write email to the json object
        jsonObject.putOpt("email", email)

        // Return
        return jsonObject
    }

    override fun fromJSON(jsonObject: JSONObject) {
        throw UnsupportedOperationException()
    }
}