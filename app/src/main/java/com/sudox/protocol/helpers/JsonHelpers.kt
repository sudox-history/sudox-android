package com.sudox.protocol.helpers

import org.json.JSONArray
import org.json.JSONException

@Throws(JSONException::class)
fun Array<*>.toJsonArray(): JSONArray {
    return JSONArray(this)
}

@Throws(JSONException::class)
fun String.toJsonArray(): JSONArray {
    return JSONArray(this)
}