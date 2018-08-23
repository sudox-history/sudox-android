package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONException
import org.json.JSONObject

class ContactDTO: JsonModel {

    // To get
    lateinit var id: String
    var firstColor: String? = null
    var secondColor: String? = null
    var avatarUrl: String? = null
    lateinit var name: String
    lateinit var nickname: String

    // Common
    var checkAvatar: Boolean = false

    override fun toJSON(): JSONObject {
        return JSONObject()
    }

    override fun fromJSON(jsonObject: JSONObject){
        try {
            val photoJsonArray = jsonObject.getJSONArray("photo")
            firstColor = photoJsonArray[0].toString()
            secondColor = photoJsonArray[1].toString()
            checkAvatar = true
        } catch (e: JSONException){
            avatarUrl = jsonObject.getString("photo")
            checkAvatar = false
        }

        id = jsonObject.optString("id")
        name = jsonObject.optString("name")
        nickname = jsonObject.optString("nickname")
    }
}