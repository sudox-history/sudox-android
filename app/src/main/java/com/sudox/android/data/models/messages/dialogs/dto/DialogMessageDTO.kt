package com.sudox.android.data.models.messages.dialogs.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class DialogMessageDTO : JsonModel() {

    var id: Long = 0
    var sender: Long = 0
    var peer: Long = 0
    lateinit var message: String
    var date: Long = 0

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optLong("id")
        sender = jsonObject.optLong("fid")
        peer = jsonObject.optLong("tid")
        message = jsonObject.optString("msg")
        date = jsonObject.optLong("date")
    }
}