package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.helpers.asIterable
import com.sudox.protocol.models.JsonModel
import org.json.JSONObject
import java.math.BigInteger

open class ContactsIdsListDTO : JsonModel() {

    // For read
    lateinit var ids: ArrayList<Long>

    override fun fromJSON(jsonObject: JSONObject) {
        ids = jsonObject
                .optJSONArray("ids")
                .asIterable()
                .filter { it is Long || it is Int } as ArrayList<Long>
    }
}