package com.sudox.protocol.helpers

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@Throws(JSONException::class)
fun Array<*>.toJsonArray(): JSONArray {
    return JSONArray(this)
}

@Throws(JSONException::class)
fun String.toJsonArray(): JSONArray {
    return JSONArray(this)
}

fun JSONArray.asSequence(): Sequence<Any> {
    return object : Sequence<Any> {
        override fun iterator() = object : Iterator<Any> {
            val it = (0 until this@asSequence.length()).iterator()

            override fun next() = this@asSequence.get( it.next())
            override fun hasNext() = it.hasNext()
        }
    }
}

fun JSONArray.forEachObject(callback: (JSONObject) -> (Unit)) {
    for (i in 0 until length()) {
        callback(getJSONObject(i))
    }
}