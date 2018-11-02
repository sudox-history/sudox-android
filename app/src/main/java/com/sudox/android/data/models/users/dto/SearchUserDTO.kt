package com.sudox.android.data.models.users.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class SearchUserDTO: JsonModel() {

    // For sending & reading
    lateinit var query: String

    // For reading
    lateinit var id: String
    lateinit var name: String
    lateinit var nickname: String
    lateinit var photo: String

    override fun toJSON(): JSONObject {
        return JSONObject().apply { putOpt("q", query) }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optString("id")
        name = jsonObject.optString("name")
        nickname = jsonObject.optString("nickname")
        photo = jsonObject.optString("photo")
    }
}