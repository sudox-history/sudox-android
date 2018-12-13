package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.helpers.asSequence
import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class ContactsIdsListDTO : JsonModel() {

    var ids: ArrayList<Long> = arrayListOf()

    override fun fromJSON(jsonObject: JSONObject) {
        ids = jsonObject.optJSONArray("ids")
                .asSequence()
                .filter { it is Long || it is Int }
                .map { it as Long }
                .toMutableList() as ArrayList<Long>
    }
}