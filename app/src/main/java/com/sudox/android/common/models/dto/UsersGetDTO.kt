package com.sudox.android.common.models.dto

import com.sudox.protocol.model.JsonModel
import org.json.JSONException
import org.json.JSONObject

class UsersGetDTO: JsonModel {

    // To send
    lateinit var id: String

    // To get
    lateinit var uid: String
    var firstColor: String? = null
    var secondColor: String? = null
    var avatarUrl: String? = null
    lateinit var name: String
    lateinit var nickname: String

    // Common
    var checkAvatar: Boolean = false
    var errorCode = -1

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("id", id)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        if(jsonObject.has("error")){
            val response = jsonObject.getJSONObject("error")
            errorCode = response.optInt("code")
        } else {
            val response = jsonObject.getJSONObject("response")
            try {
                val photoJsonArray = response.getJSONArray("photo")
                firstColor = photoJsonArray[0].toString()
                secondColor = photoJsonArray[1].toString()
                checkAvatar = true
            } catch (e: JSONException) {
                avatarUrl = response.getString("photo")
                checkAvatar = false
            }

            id = response.optString("id")
            name = response.optString("name")
            nickname = response.optString("nickname")
        }
    }
}