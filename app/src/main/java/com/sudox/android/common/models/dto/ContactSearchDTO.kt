package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONException
import org.json.JSONObject

class ContactSearchDTO: JsonModel {

    //To send
    var email: String? = null
    var id: String? = null

    //To get
    lateinit var scid: String
    lateinit var name: String
    lateinit var status: String
    var firstColor: String? = null
    var secondColor: String? = null
    var avatarUrl: String? = null

    // Common
    var checkAvatar: Boolean = false
    var errorCode = -1

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            if (email != null)
                putOpt("email", email)
            else
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
            name = response.optString("name")
            scid = response.optString("id")
        }
    }
}