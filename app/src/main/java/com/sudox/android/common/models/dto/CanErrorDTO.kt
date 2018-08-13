package com.sudox.android.common.models.dto

import com.sudox.protocol.model.dto.JsonModel
import org.json.JSONObject

abstract class CanErrorDTO : JsonModel {

    var errorCode: Int = 0
    lateinit var errorMessage: String

    fun isError(): Boolean {
        return errorCode != 0
    }

    abstract override fun toJSON(): JSONObject

    override fun fromJSON(jsonObject: JSONObject) {
        errorCode = jsonObject.optInt("errorCode")
        errorMessage = jsonObject.optString("errorMsg")
    }
}