package com.sudox.android.common.models.dto

import org.json.JSONObject

class ConfirmCodeDTO : CanErrorDTO() {

    var code: Int = 0
    var codeStatus: Int = 0

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("code", code)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)
        codeStatus = jsonObject.getInt("code")
    }
}