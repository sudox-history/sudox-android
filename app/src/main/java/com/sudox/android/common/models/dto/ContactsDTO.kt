package com.sudox.android.common.models.dto

import org.json.JSONException
import org.json.JSONObject

class ContactsDTO : CanErrorDTO() {


    var offset: Int = 0
    var count: Int = 0

    lateinit var id: String
    lateinit var firstColor: String
    lateinit var secondColor: String
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
            val photoJsonArray = jsonObject.getJSONArray("photo")
            firstColor = photoJsonArray[0].toString()
            secondColor = photoJsonArray[1].toString()
        } catch (e: JSONException){
            avatarUrl = jsonObject.getString("photo")
        }

        id = jsonObject.optString("id")
        name = jsonObject.optString("name")
        nickname = jsonObject.optString("nickname")
    }
}