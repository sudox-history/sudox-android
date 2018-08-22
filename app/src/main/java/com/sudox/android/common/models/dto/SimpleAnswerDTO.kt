package com.sudox.android.common.models.dto

import org.json.JSONObject

class SimpleAnswerDTO : CanErrorDTO() {

    var code: Int = 0

    override fun toJSON(): JSONObject {
        return JSONObject()
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)
        code = jsonObject.optInt("code")
    }

}