package com.sudox.android.common.models.dto

import org.json.JSONArray
import org.json.JSONObject

class ContactsGetDTO : CanErrorDTO() {

    var offset: Int = 0
    var count: Int = 0

    var code: Int = 0
    var items: JSONArray? = null

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            putOpt("offset", offset)
            putOpt("count", count)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)
        code = jsonObject.optInt("code")
        if(code != 0) {
            items = jsonObject.optJSONArray("items")
        }
    }
}