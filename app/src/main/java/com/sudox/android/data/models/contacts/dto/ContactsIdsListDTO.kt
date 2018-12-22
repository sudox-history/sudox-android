package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.helpers.asSequence
import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

open class ContactsIdsListDTO : JsonModel() {

    var ids: ArrayList<Long> = arrayListOf()

    override fun fromJSON(jsonObject: JSONObject) {
        ids = jsonObject
                .optJSONArray("ids")
                .asSequence()
                .filter { it is Long || it is Int }
                .toMutableList() as ArrayList<Long>
    }
}