package com.sudox.android.data.models.contacts.dto

import org.json.JSONArray
import org.json.JSONObject

class ContactsSyncDTO : ContactsIdsListDTO() {

    // Contacts for sync ...
    var items: ArrayList<ContactPairDTO> = arrayListOf()

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("items", JSONArray().apply {
                items.forEach { put(it.toJSON()) }
            })
        }
    }
}