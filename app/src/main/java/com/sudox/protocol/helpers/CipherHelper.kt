package com.sudox.protocol.helpers

import android.util.Base64
import com.sudox.protocol.ELLIPTIC_CURVE_PARAM
import com.sudox.protocol.SIGN_PUBLIC_KEY
import java.security.*
import java.security.interfaces.ECPublicKey
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.math.BigInteger
import java.security.spec.*

fun readSignaturePublicKey(keyBody: String): PublicKey {
    val keyFactory = KeyFactory.getInstance("EC")
    val decoded = Base64.decode(keyBody, Base64.NO_PADDING)
    val x509EncodedKeySpec = X509EncodedKeySpec(decoded)

    return keyFactory.generatePublic(x509EncodedKeySpec)
}

fun generateKeys(): KeyPair {
    return with(KeyPairGenerator.getInstance("EC")) {
        initialize(ELLIPTIC_CURVE_PARAM, SecureRandom())
        generateKeyPair()
    }
}

fun ECPublicKey.getPoint(): ByteArray {
    val affineXBytes = removeLeadingZeros(w.affineX.toByteArray())
    val affineYBytes = removeLeadingZeros(w.affineY.toByteArray())
    val encodedBytes = ByteArray(48 * 2 + 1)
    encodedBytes[0] = 0x04
    System.arraycopy(affineXBytes, 0, encodedBytes, 48 - affineXBytes.size + 1, affineXBytes.size)
    System.arraycopy(affineYBytes, 0, encodedBytes, encodedBytes.size - affineYBytes.size, affineYBytes.size)
    return encodedBytes
}

fun readPublicKey(keyBody: String): ECPublicKey {
    val decoded = Base64.decode(keyBody, Base64.NO_PADDING)
    val keyFactory = KeyFactory.getInstance("EC")

    val x = ByteArray(49)
    val y = ByteArray(49)
    System.arraycopy(decoded, 1, x, 1, 48)
    System.arraycopy(decoded, 49, y, 1, 48)

    // Get X & Y coords
    val ecPublicKeySpec = ECPublicKeySpec(ECPoint(BigInteger(x), BigInteger(y)), ELLIPTIC_CURVE_PARAM)

    // Generate key
    return keyFactory.generatePublic(ecPublicKeySpec) as ECPublicKey
}

@Throws(IllegalArgumentException::class)
fun verifyData(data: String, signature: String, key: PublicKey = SIGN_PUBLIC_KEY): Boolean {
    val verifier = Signature.getInstance("SHA224withECDSA")
    val decodedSignature = Base64.decode(signature, Base64.NO_PADDING)
    val decodedKey = Base64.decode(data, Base64.NO_PADDING)

    // Init verifier
    with(verifier) {
        initVerify(key)
        update(decodedKey)
    }

    return try {
        verifier.verify(decodedSignature)
    } catch (e: SignatureException) {
        false
    }
}

fun decryptAES(key: ByteArray, iv: String, data: String): String {
    val keySpec = SecretKeySpec(key, "AES")
    val decodedIv = Base64.decode(iv, Base64.NO_PADDING)
    val parameterSpec = IvParameterSpec(decodedIv)
    val cipher = Cipher.getInstance("AES/CTR/NoPadding")

    // Decrypt data
    return with(cipher) {
        init(Cipher.DECRYPT_MODE, keySpec, parameterSpec)

        // To string
        String(cipher.doFinal(Base64.decode(data, Base64.NO_PADDING)))
    }
}

@Throws(IllegalArgumentException::class, InvalidKeyException::class, IllegalBlockSizeException::class)
fun encryptAES(key: ByteArray, iv: String, data: String): String {
    val keySpec = SecretKeySpec(key, "AES")
    val decodedIv = Base64.decode(iv, Base64.NO_PADDING)
    val parameterSpec = IvParameterSpec(decodedIv)
    val cipher = Cipher.getInstance("AES/CTR/NoPadding")

    return with(cipher) {
        init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec)
        val bytes = cipher.doFinal(data.toByteArray())
        val encodedBytes = Base64.encode(bytes, Base64.NO_WRAP)

        // Encode to string
        String(encodedBytes)
    }
}

fun calculateHMAC(secretKey: ByteArray, message: String): ByteArray {
    return with(Mac.getInstance("HmacSHA224")) {
        init(SecretKeySpec(secretKey, "HmacSHA224"))
        doFinal(message.toByteArray())
    }
}

fun calculateHash(input: ByteArray): ByteArray {
    return MessageDigest
            .getInstance("SHA-224")
            .digest(input)
}

fun calculateSecretKey(privateKey: PrivateKey, publicKey: PublicKey): ByteArray {
    val keyAgree = KeyAgreement.getInstance("ECDH").apply {
        init(privateKey, SecureRandom())
        doPhase(publicKey, true)
    }

    return keyAgree.generateSecret()
}

fun randomBase64String(length: Int): String {
    val bytes = ByteArray(length)

    // Генерируем рандомные байты
    SecureRandom().nextBytes(bytes)

    // Переводим в Base-64
    return Base64
            .encodeToString(bytes, Base64.DEFAULT)
            .replace("\n", "")
}