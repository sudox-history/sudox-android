package com.sudox.protocol.helper

import java.security.MessageDigest

fun getHash(input: ByteArray): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")

    // Get hash bytes
    val hashBytes = digest.digest(input)

    // Convert to the hex and return result
    return encodeHex(hashBytes)
}

fun getHashString(input: String): String {
    val inputBytes = input.toByteArray()

    // Get the hash
    val hashBytes = getHash(inputBytes)

    // Convert bytes to the string and return result
    return String(hashBytes)
}