package com.sudox.android.common.models.dto

import org.json.JSONException
import org.json.JSONObject

class ContactDTO : CanErrorDTO() {

    var offset: Int = 0
    var count: Int = 0

    lateinit var id: String
    var firstColor: String? = null
    var secondColor: String? = null
    var avatarUrl: String? = null
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
        super.fromJSON(jsonObject)
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