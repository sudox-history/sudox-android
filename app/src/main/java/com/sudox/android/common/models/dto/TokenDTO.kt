package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

class TokenDTO(val token: String) : CanErrorDTO() {

    var id: Int = 0
    var code: Int = 0

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("token", token)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        with(jsonObject) {
            id = optInt("id")
            code = optInt("code")
        }
    }

}