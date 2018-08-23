package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ContactsGetDTO: JsonModel {

    // To get
    var code: Int = 0
    var items: JSONArray? = null

    // Common
    var errorCode = 0

    override fun toJSON(): JSONObject {
        return JSONObject()
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if(jsonObject.has("error")){
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            try {
                items = jsonObject.optJSONArray("response")
            } catch (e: JSONException){
                code = jsonObject.getInt("response")
            }
        }
    }
}