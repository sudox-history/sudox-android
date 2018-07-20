package com.sudox.protocol.helper

import com.sudox.protocol.model.PerformedDataForEncrypt
import com.sudox.protocol.model.SymmetricKey
import org.json.JSONObject

fun performDataForEncrypt(symmetricKey: SymmetricKey, event: String, message: String): PerformedDataForEncrypt {
    // Get the hash
    val hash = getHashString(symmetricKey.random + getHashString(event) + getHashString(message))

    // Create the payload object
    val payloadObject = with(JSONObject()) {
        put("random", symmetricKey.random)
        put("event", event)
        put("msg", message)
    }

    // Create result object
    return PerformedDataForEncrypt(hash, payloadObject)
}