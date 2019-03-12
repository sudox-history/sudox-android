package com.sudox.protocol.models

import org.json.JSONObject

abstract class JsonModel {

    var error: Int = -1
    var response: Int = 0

    open fun toJSON(): JSONObject? = null
    open fun fromJSON(jsonObject: JSONObject) {}
    fun containsError(): Boolean = error != -1
    fun isSuccess(): Boolean = error == -1 || response == 1

    internal fun readResponse(jsonObject: JSONObject) {
        if (jsonObject.has("error")) {
            error = jsonObject.optInt("error")
        } else {
            val value = jsonObject.opt("response")

            // Parse ...
            when (value) {
                null -> fromJSON(jsonObject)
                is Int -> response = value
            }
        }
    }
}