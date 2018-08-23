package com.sudox.protocol.helper

import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import javax.crypto.AEADBadTagException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

// Read public key
fun readPublicKey(key: String): PublicKey {
    val keyFactory = KeyFactory.getInstance("RSA")
    val keyBody = key
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n", "")

    // Decode key key
    val decoded = decodeBase64String(keyBody)

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
        encodeBase64(bytes)
    }

    return String(bytes)
}

// Encrypt AES
@Throws(IllegalArgumentException::class, InvalidKeyException::class, IllegalBlockSizeException::class)
fun encryptAES(key: String, iv: String, data: String): String {
    val decodedKey = decodeBase64String(key)
    val keySpec = SecretKeySpec(decodedKey, "AES")

    // IV parameters
    val decodedIv = decodeBase64String(iv)
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
}

// Decrypt AES
@Throws(IllegalArgumentException::class, InvalidKeyException::class, IllegalBlockSizeException::class, BadPaddingException::class, AEADBadTagException::class)
fun decryptAES(key: String, iv: String, data: String): String {
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
}