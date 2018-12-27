package com.sudox.android.data.models.users.dto

import com.sudox.protocol.helpers.forEachObject
import com.sudox.protocol.models.JsonModel
import org.json.JSONArray
import org.json.JSONObject

class UserInfoDTO : JsonModel() {

    // For sending & reading
    var id: Long = 0
    var ids: List<Long>? = null

    // For reading
    var users: ArrayList<UserInfoDTO>? = null
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
        users = arrayListOf()

        // Mapping
        jsonObject
                .optJSONArray("users")
                .forEachObject {
                    users!!.plusAssign(UserInfoDTO().apply {
                        id = it.optLong("id")
                        name = it.optString("name")
                        nickname = it.optString("nickname")
                        photo = it.optString("photo")

                        if (it.has("phone"))
                            phone = it.optString("phone")
                        if (it.has("status"))
                            status = it.optString("status")
                        if (it.has("bio"))
                            bio = it.optString("bio")
                    })
                }
    }
}