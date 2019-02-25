package com.sudox.protocol.helpers

import android.util.Base64
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


private val SIGN_PUBLIC_KEY_INSTANCE by lazy { readSignPublicKey(SIGN_PUBLIC_KEY) }

private const val SIGN_PUBLIC_KEY = "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAEflkmgol1o7GFRjjB72BBbqhsRSI1SwHK" +
        "/7357yJaEzwrBUt231AiPD2AG2MNaXr8SqDCUv3jbLzOB4+/bVkcimZVP2elvjsp" +
        "/AdU1335LpuCufavSCrftkzD0MeiUBqc"

private val COFACTOR = 1
private val P384 = ECParameterSpec(
        EllipticCurve(
                // field the finite field that this elliptic curve is over.
                ECFieldFp(BigInteger("39402006196394479212279040100143613805079739270465" +
                        "44666794829340424572177149687032904726608825893800" +
                        "1861606973112319")),
                // a the first coefficient of this elliptic curve.
                BigInteger("39402006196394479212279040100143613805079739270465" +
                        "44666794829340424572177149687032904726608825893800" +
                        "1861606973112316"),
                // b the second coefficient of this elliptic curve.
                BigInteger("27580193559959705877849011840389048093056905856361" +
                        "56852142870730198868924130986086513626076488374510" +
                        "7765439761230575")
        ),
        //g the generator which is also known as the base point.
        ECPoint(
                // gx
                BigInteger("26247035095799689268623156744566981891852923491109" +
                        "21338781561590092551885473805008902238805397571978" +
                        "6650872476732087"),
                // gy
                BigInteger("83257109614890299855467512895201081792878530488613" +
                        "15594709205902480503199884419224438643760392947333" +
                        "078086511627871")
        ),
        // Order n
        BigInteger("39402006196394479212279040100143613805079739270465446667946905279627" + "659399113263569398956308152294913554433653942643"),
        COFACTOR)

private fun readSignPublicKey(keyBody: String): PublicKey {
    val keyFactory = KeyFactory.getInstance("EC")
    val decoded = Base64.decode(keyBody, Base64.NO_PADDING)
    val x509EncodedKeySpec = X509EncodedKeySpec(decoded)

    return keyFactory.generatePublic(x509EncodedKeySpec)
}

fun generateECDHPair(): KeyPair {
    val ecGenParameterSpec = ECGenParameterSpec("secp384r1")

    return with(KeyPairGenerator.getInstance("EC")) {
        initialize(ecGenParameterSpec, SecureRandom())
        generateKeyPair()
    }
}

fun ECPublicKey.getPoint(): ByteArray {
    val affineXBytes = stripLeadingZeros(w.affineX.toByteArray())
    val affineYBytes = stripLeadingZeros(w.affineY.toByteArray())
    val encodedBytes = ByteArray(48 * 2 + 1)
    encodedBytes[0] = 0x04 //uncompressed
    System.arraycopy(affineXBytes, 0, encodedBytes, 48 - affineXBytes.size + 1, affineXBytes.size)
    System.arraycopy(affineYBytes, 0, encodedBytes, encodedBytes.size - affineYBytes.size, affineYBytes.size)
    return encodedBytes
}

fun readECDHPublicKey(keyBody: String): ECPublicKey {
    val decoded = Base64.decode(keyBody, Base64.NO_PADDING)
    val keyFactory = KeyFactory.getInstance("EC")

    val x = ByteArray(49)
    val y = ByteArray(49)
    System.arraycopy(decoded, 1, x, 1, 48)
    System.arraycopy(decoded, 49, y, 1, 48)

    // Get X & Y coords
    val ecPublicKeySpec = ECPublicKeySpec(ECPoint(BigInteger(x), BigInteger(y)), P384)

    // Generate key
    return keyFactory.generatePublic(ecPublicKeySpec) as ECPublicKey
}

fun stripLeadingZeros(bytes: ByteArray): ByteArray {
    var i = 0

    while (i < bytes.size - 1) {
        if (bytes[i].toInt() != 0) {
            break
        }

        i++
    }

    return if (i == 0) {
        bytes
    } else {
        val stripped = ByteArray(bytes.size - i)
        System.arraycopy(bytes, i, stripped, 0, stripped.size)
        stripped
    }
}

fun generateECDHSecretKey(privateKey: PrivateKey, publicKey: PublicKey): ByteArray {
    val keyAgree = KeyAgreement.getInstance("ECDH").apply {
        init(privateKey, SecureRandom())
        doPhase(publicKey, true)
    }

    return keyAgree.generateSecret()
}

fun getHash(input: ByteArray): ByteArray {
    return MessageDigest.getInstance("SHA-224").digest(input)
}

@Throws(IllegalArgumentException::class)
fun verifyData(key: String, signature: String): Boolean {
    val verifier = Signature.getInstance("SHA224withECDSA")
    val decodedSignature = Base64.decode(signature, Base64.NO_PADDING)
    val decodedKey = Base64.decode(key, Base64.NO_PADDING)

    // Init verifier
    with(verifier) {
        initVerify(SIGN_PUBLIC_KEY_INSTANCE)
        update(decodedKey)
    }

    return verifier.verify(decodedSignature)
}

fun decryptAES(key: ByteArray, iv: String, data: String): String {
    val keySpec = SecretKeySpec(key, "AES")
    val decodedIv = Base64.decode(iv, Base64.NO_PADDING)
    val parameterSpec = IvParameterSpec(decodedIv)
    val cipher = Cipher.getInstance("AES/CTR/NoPadding")

    // Decrypt data
    return with(cipher) {
        init(Cipher.DECRYPT_MODE, keySpec, parameterSpec)
        val bytes = cipher.doFinal(Base64.decode(data, Base64.NO_PADDING))

        // To string
        String(bytes)
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

fun getHmac(secretKey: ByteArray, message: String): ByteArray {
    return with(Mac.getInstance("HmacSHA224")) {
        init(SecretKeySpec(secretKey, "HmacSHA224"))
        doFinal(message.toByteArray())
    }
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