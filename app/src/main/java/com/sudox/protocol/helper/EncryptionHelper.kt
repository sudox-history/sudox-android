package com.sudox.protocol.helper

import java.security.PublicKey
import java.security.Signature

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