package com.sudox.protocol.model

import com.sudox.android.common.models.ErrorType
import org.json.JSONObject

abstract class JsonModel {

    // Error codeStatus & codeStatus status
    var error: ErrorType? = null
    var response = 0

    abstract fun toJSON(): JSONObject
    abstract fun fromJSON(jsonObject: JSONObject)

    fun readFromJSON(jsonObject: JSONObject) {
        if (jsonObject.has("error")) {
            error = ErrorType.findByCode(jsonObject.optInt("error"))
        } else if (jsonObject.has("response")) {
            val value = jsonObject.opt("response")

            when (value) {
                null -> fromJSON(jsonObject)
                is Int -> response = value
                is JSONObject -> fromJSON(value)
            }
        }
    }

    fun containsError(): Boolean {
        return error != null
    }

    fun isSuccess(): Boolean {
        return error == null || response == 1
    }
}