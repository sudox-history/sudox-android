package com.sudox.protocol.helper

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
    val decoded = decodeBase64String(body)

    // Create key spec
    val x509EncodedKeySpec = X509EncodedKeySpec(decoded)

    // Build the public key instance
    return keyFactory.generatePublic(x509EncodedKeySpec)
}

// Verify data
fun verifyData(publicKey: PublicKey, signature: ByteArray, data: ByteArray): Boolean {
    return try {
        val verifier = Signature.getInstance("SHA256withRSA")

        // Init verifier
        with(verifier) {
            initVerify(publicKey)
            update(data)
        }

        verifier.verify(signature)
    } catch (ex: Exception) {
        false
    }
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
        encodeBase64(bytes)
    }

    return String(bytes)
}

// Encrypt AES
fun encryptAES(key: String, iv: String, data: String): String? {
    try {
        val decodedKey = decodeBase64String(key) ?: return null
        val keySpec = SecretKeySpec(decodedKey, "AES")

        // IV parameters
        val decodedIv = decodeBase64String(iv) ?: return null
        val parameterSpec = IvParameterSpec(decodedIv)

        // Get the cipher instance
        val cipher = Cipher.getInstance("AES/CTR/NoPadding")

        // Encrypt data
        return with(cipher) {
            init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec)

            // Encrypt data
            val bytes = cipher.doFinal(data.toByteArray())
            val encodedBytes = encodeBase64(bytes)

            // Encode the hex
            String(encodedBytes)
        }
    } catch (ex: Exception) {
        return null
    }
}

// Decrypt AES
fun decryptAES(key: String, iv: String, data: String): String? {
    try {
        val keySpec = SecretKeySpec(decodeBase64String(key), "AES")

        // Get the cipher instance
        val cipher = Cipher.getInstance("AES/CTR/NoPadding")

        // IV parameters
        val parameterSpec = IvParameterSpec(decodeBase64String(iv))

        // Encrypt data
        return with(cipher) {
            init(Cipher.DECRYPT_MODE, keySpec, parameterSpec)

            // Encrypt data
            val bytes = cipher.doFinal(decodeBase64(data.toByteArray()))

            // To string
            String(bytes)
        }
    } catch (e: Exception) {
        return null
    }
}