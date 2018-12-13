package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONArray

class ContactsSyncDTO : JsonModel() {

    var items: ArrayList<ContactPairDTO> = arrayListOf()

    override fun toJSONArray(): JSONArray {
        return JSONArray().apply { items.forEach { put(it.toJSON()) } }
    }
}