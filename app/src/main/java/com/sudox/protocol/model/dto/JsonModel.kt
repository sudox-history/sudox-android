package com.sudox.protocol.model.dto

import org.json.JSONObject

interface JsonModel {
    fun toJSON() : JSONObject
    fun fromJSON(jsonObject: JSONObject)
}