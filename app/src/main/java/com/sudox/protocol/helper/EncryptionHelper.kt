package com.sudox.protocol.helper

import android.util.Base64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

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

// Encrypt RSA
fun encryptRSA(publicKey: PublicKey, data: String): String {
    val cipher = Cipher.getInstance("RSA/NONE/OAEPPadding")

    // Encode
    val bytes = with(cipher) {
        init(Cipher.ENCRYPT_MODE, publicKey)

        // Encode with RSA
        val bytes = doFinal(data.toByteArray())

        // Encode to the HEX
        encodeHexBytes(bytes)
    }

    return String(bytes)
}

// Encrypt AES
fun encryptAES(key: String, iv: String, data: String): String {
    val keySpec = SecretKeySpec(decodeHex(key), "AES")

    // Get the cipher instance
    val cipher = Cipher.getInstance("AES/CTR/NoPadding")

    // IV parameters
    val parameterSpec = IvParameterSpec(decodeHex(iv))

    // Encrypt data
    val bytes = with(cipher) {
        init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec)

        // Encrypt data
        val bytes = cipher.doFinal(data.toByteArray())

        // Encode the hex
        encodeHexBytes(bytes)
    }

    return String(bytes)
}