package com.sudox.android.common.models.dto

import org.json.JSONObject

class ContactAddDTO : CanErrorDTO() {

    var id: String? = null

    lateinit var aid: String
    var code: Int = 0

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("id", id)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)
        code = jsonObject.optInt("code")
        if (code != 0)
            aid = jsonObject.optString("id")
    }
}