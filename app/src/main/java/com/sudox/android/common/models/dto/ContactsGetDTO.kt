package com.sudox.android.common.models.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONArray
import org.json.JSONObject

class ContactsGetDTO: JsonModel {

    // To get
    var items: JSONArray? = null

    // Common
    var errorCode = -1

    override fun toJSON(): JSONObject {
        return JSONObject()
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if(jsonObject.has("error")){
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            items = jsonObject.optJSONArray("response")
        }
    }
}