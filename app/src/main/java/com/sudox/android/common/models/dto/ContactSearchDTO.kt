package com.sudox.android.common.models.dto

import org.json.JSONException
import org.json.JSONObject

class ContactSearchDTO : CanErrorDTO() {

    lateinit var nickname: String

    lateinit var scid: String
    lateinit var name: String
    lateinit var status: String
    var firstColor: String? = null
    var secondColor: String? = null
    var avatarUrl: String? = null

    var checkAvatar: Boolean = false

    var code: Int = 0

    override fun toJSON(): JSONObject {
        return with(JSONObject()) {
            putOpt("nickname", nickname)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        super.fromJSON(jsonObject)
        if (jsonObject.getInt("code") == 1) {
            val user = jsonObject.getJSONObject("user")
            try {
                val photoJsonArray = user.getJSONArray("photo")
                firstColor = photoJsonArray[0].toString()
                secondColor = photoJsonArray[1].toString()
                checkAvatar = true
            } catch (e: JSONException) {
                avatarUrl = user.getString("photo")
                checkAvatar = false
            }
            name = user.optString("name")
            scid = user.optString("id")
            code = 1
        } else {
            code = 0
        }
    }
}