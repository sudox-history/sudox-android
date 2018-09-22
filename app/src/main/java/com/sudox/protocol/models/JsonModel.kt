package com.sudox.protocol.models

import org.json.JSONObject

abstract class JsonModel {

    // Error codeStatus & codeStatus status
    var error = -1
    var response = 0

    abstract fun toJSON(): JSONObject
    abstract fun fromJSON(jsonObject: JSONObject)

    fun readFromJSON(jsonObject: JSONObject) {
        if (jsonObject.has("error")) {
            error = jsonObject.optInt("error")
        } else {
            val value = jsonObject.opt("response")

            when (value) {
                null -> fromJSON(jsonObject)
                is Int -> response = value
                is JSONObject -> fromJSON(value)
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