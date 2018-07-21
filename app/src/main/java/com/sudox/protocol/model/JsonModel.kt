package com.sudox.protocol.model

import org.json.JSONObject

interface JsonModel {
    fun toJSON() : JSONObject
    fun fromJSON(jsonObject: JSONObject)
}