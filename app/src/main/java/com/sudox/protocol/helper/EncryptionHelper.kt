package com.sudox.protocol.helper

import android.util.Base64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

// Read public key
fun readPublicKey(body: String): PublicKey {
    val keyFactory = KeyFactory.getInstance("RSA")

    // Decode key body
    val decoded = Base64.decode(body, Base64.DEFAULT)

    // Create key spec
    val x509EncodedKeySpec = X509EncodedKeySpec(decoded)

    // Build the public key instance
    return keyFactory.generatePublic(x509EncodedKeySpec)
}

// Verify data
fun verifyData(publicKey: PublicKey, signature: ByteArray, data: ByteArray): Boolean {
    val verifier = Signature.getInstance("SHA256withRSA")

    // Init verifier
    with(verifier) {
        initVerify(publicKey)
        update(data)
    }

    return verifier.verify(signature)
}