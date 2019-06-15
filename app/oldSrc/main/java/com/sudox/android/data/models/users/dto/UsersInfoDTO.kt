package com.sudox.android.data.models.users.dto

import com.sudox.protocol.helpers.asIterable
import com.sudox.protocol.models.JsonModel
import org.json.JSONArray
import org.json.JSONObject

class UsersInfoDTO : JsonModel() {

    // For sending / reading
    var ids: List<Long>? = null
    var users: ArrayList<UserDTO>? = null

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            putOpt("ids", JSONArray(ids))
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        users = jsonObject
                .optJSONArray("users")
                .asIterable()
                .map { UserDTO().apply { fromJSON(it as JSONObject) } } as ArrayList<UserDTO>
    }
}