package com.sudox.android.data.models.users.dto

import com.sudox.protocol.helpers.forEachObject
import com.sudox.protocol.models.JsonModel
import org.json.JSONArray
import org.json.JSONObject

class UserInfoDTO : JsonModel() {

    // For sending & reading
    lateinit var id: String
    var ids: List<String>? = null

    // For reading
    lateinit var users: ArrayList<UserInfoDTO>
    lateinit var name: String
    lateinit var nickname: String
    lateinit var photo: String

    var phone: String? = null
    var status: String? = null
    var bio: String? = null

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            if (ids != null) {
                putOpt("ids", JSONArray(ids))
            } else {
                putOpt("id", id)
            }
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optString("id")
        name = jsonObject.optString("name")
        nickname = jsonObject.optString("nickname")
        photo = jsonObject.optString("photo")

        if (jsonObject.has("phone"))
            phone = jsonObject.optString("phone")
        if (jsonObject.has("status"))
            status = jsonObject.optString("status")
        if (jsonObject.has("bio"))
            bio = jsonObject.optString("bio")
    }

    override fun fromJSONArray(jsonArray: JSONArray) {
        users = arrayListOf()

        // Mapping
        jsonArray.forEachObject {
            users.plusAssign(UserInfoDTO().apply { fromJSON(it) })
        }
    }
}