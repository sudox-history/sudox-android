package com.sudox.protocol.helper

import com.sudox.protocol.model.PerformedDataForClient
import com.sudox.protocol.model.PerformedDataForEncrypt
import com.sudox.protocol.model.SymmetricKey
import org.json.JSONObject

fun prepareDataForEncrypt(symmetricKey: SymmetricKey, event: String, message: String): PerformedDataForEncrypt {
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

fun checkHashes(serverHash: String, payload: String): Boolean {
    // JSON object for payload
    val payloadObject = JSONObject(payload)

    // Parse payload
    val event = payloadObject.optString("event")
    val random = payloadObject.optString("random")
    val message = payloadObject.optString("msg")

    // Get hash
    val clientHash = getHashString(random + getHashString(event) + getHashString(message))

    // Check, that hashes are equals
    return clientHash == serverHash
}

fun prepareDataForClient(payload: String): PerformedDataForClient {
    // JSON object for payload
    val payloadObject = JSONObject(payload)

    // Parse payload
    val event = payloadObject.optString("event")
    val message = payloadObject.optString("msg")

    return PerformedDataForClient(event, message)
}