package com.sudox.android.common.models.dto

import org.json.JSONException
import org.json.JSONObject

class ContactsDTO : CanErrorDTO() {


    var offset: Int = 0
    var count: Int = 0

    var id: Long = 0
    lateinit var avatarJson: JSONObject
    lateinit var avatarUrl: String
    lateinit var name: String
    lateinit var nickname: String

    var checkAvatar: Boolean = false

    override fun toJSON(): JSONObject {
        return with(JSONObject()){
            put("offset", offset)
            put("count", count)
        }

    }

    override fun fromJSON(jsonObject: JSONObject){
        try {
            avatarJson  = jsonObject.getJSONObject("photo")
            checkAvatar = true
        } catch (e: JSONException){
            avatarUrl = jsonObject.getString("photo")
        }
        id = jsonObject.optLong("id")
        name = jsonObject.optString("name")
        nickname = jsonObject.optString("nickname")
    }

}