package com.sudox.android.common.models.dto

import org.json.JSONObject

class ContactAddRemoveDTO : CanErrorDTO() {

    var sendId: String? = null

    lateinit var id: String
    var code: Int = 0

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("id", sendId)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)
        code = jsonObject.optInt("code")
        if (code != 0)
            id = jsonObject.optString("id")
    }
}