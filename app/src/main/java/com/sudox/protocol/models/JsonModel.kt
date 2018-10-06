package com.sudox.protocol.models

import org.json.JSONArray
import org.json.JSONObject

abstract class JsonModel {

    // Error codeStatus & codeStatus status
    var error = -1
    var response = 0

    open fun toJSON(): JSONObject { throw UnsupportedOperationException() }
    open fun fromJSON(jsonObject: JSONObject) {}
    open fun fromJSONArray(jsonArray: JSONArray) {}

    fun readResponse(jsonObject: JSONObject) {
        if (jsonObject.has("error")) {
            error = jsonObject.optInt("error")
        } else {
            val value = jsonObject.opt("response")

            when (value) {
                null -> fromJSON(jsonObject)
                is Int -> response = value
                is JSONObject -> fromJSON(value)
                is JSONArray -> fromJSONArray(value)
            }
        }
    }

    fun containsError(): Boolean {
        return error != -1
    }

    fun isSuccess(): Boolean {
        return error == -1 || response == 1
    }
}