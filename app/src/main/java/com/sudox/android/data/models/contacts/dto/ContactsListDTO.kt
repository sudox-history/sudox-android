package com.sudox.android.data.models.contacts.dto

import com.sudox.protocol.helpers.asSequence
import com.sudox.protocol.models.JsonModel
import org.json.JSONArray
import org.json.JSONObject

class ContactsListDTO : JsonModel() {

    var contacts: ArrayList<ContactInfoDTO> = arrayListOf()

    override fun fromJSONArray(jsonArray: JSONArray) {
        jsonArray.asSequence().forEach {
            if (it !is JSONObject) return@forEach

            // Read user data
            val contactInfoDTO = ContactInfoDTO().apply { fromJSON(it) }

            // Add to list
            contacts.plusAssign(contactInfoDTO)
        }
    }
}