package com.sudox.protocol.helpers

import android.util.Base64
import java.math.BigInteger
import java.nio.ByteBuffer
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.interfaces.DHPublicKey
import javax.crypto.spec.DHParameterSpec
import javax.crypto.spec.DHPublicKeySpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val SIGN_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvR9FiI9iaw9oiTFCCGwQ" +
        "Xej4Rmkg8w1pG2M+GdrtQC2pRxTFlEnO5iAHT7mqnQb29zAJbp0Jx8Z+UmypyCI9" +
        "hn0EimQLvcBTynqI1aMPMJmzemdF5vnm3GCyuWvOKqE66Z9hj+ilVqOn0KKgVq2e" +
        "j5DN1Iv9xv28+QRm70BFTowZ15ISazoGX3j7lUPFpDuiz4vfX/CbN4D8wnboqugm" +
        "I2UEuF5uRs9IGDIWZZDmpSmlEZuqAHQQA+Mvzl3P0eqMfnhV0NlA1xPcVLNdwCbP" +
        "lAI1W5zrwAG5Q5zzN+NlQeGxi92Q+NrQCTeZpL5fkRMfT0d21gNh6Qg4PjgBirSS" +
        "TQIDAQAB"

private val SIGN_PUBLIC_KEY_INSTANCE by lazy { readSignPublicKey(SIGN_PUBLIC_KEY) }

private val G = BigInteger("02", 16)
private val P = BigInteger("ffffffffffffffffc90fdaa22168c234c4c6628b80dc1cd129024e" +
        "088a67cc74020bbea63b139b22514a08798e3404ddef9519b3cd3a431b302b0a6df25f14374fe1356d" +
        "6d51c245e485b576625e7ec6f44c42e9a637ed6b0bff5cb6f406b7edee386bfb5a899fa5ae9f24117c4b" +
        "1fe649286651ece45b3dc2007cb8a163bf0598da48361c55d39a69163fa8fd24cf5f83655d23dca3ad961c" +
        "62f356208552bb9ed529077096966d670c354e4abc9804f1746c08ca18217c32905e462e36ce3be39e772c1" +
        "80e86039b2783a2ec07a28fb5c55df06f4c52c9de2bcbf6955817183995497cea956ae515d2261898fa05101" +
        "5728e5a8aacaa68ffffffffffffffff", 16)

private fun readSignPublicKey(keyBody: String): PublicKey {
    val keyFactory = KeyFactory.getInstance("RSA")
    val decoded = Base64.decode(keyBody, Base64.NO_PADDING)
    val x509EncodedKeySpec = X509EncodedKeySpec(decoded)

    return keyFactory.generatePublic(x509EncodedKeySpec)
}

fun generateDhPair(): KeyPair {
    val dhParameterSpec = DHParameterSpec(P, G)

    return with(KeyPairGenerator.getInstance("DH")) {
        initialize(dhParameterSpec, SecureRandom())
        generateKeyPair()
    }
}

@Throws(InvalidKeySpecException::class)
fun readDhPublicKey(y: String): PublicKey {
    val yBytes = Base64.decode(y, Base64.NO_WRAP)
    val keyFactory = KeyFactory.getInstance("DH")
    val keySpec = DHPublicKeySpec(bytesToBigInteger(yBytes), P, G)

    return keyFactory.generatePublic(keySpec)
}

fun bytesToBigInteger(bytes: ByteArray): BigInteger {
    val key = ByteBuffer.allocate(bytes.size + 1)
    key.put(0x00.toByte())
    key.put(bytes)

    return BigInteger(key.array())
}

fun publicKeyToBytes(publicKey: DHPublicKey): ByteArray {
    var bytes = publicKey.y.toByteArray()

    if (bytes.size % 8 != 0 && bytes[0].toInt() == 0x00) {
        bytes = Arrays.copyOfRange(bytes, 1, bytes.size)
    }

    return bytes
}

fun generateDhSecretKey(privateKey: PrivateKey, publicKey: PublicKey): ByteArray {
    val keyAgree = KeyAgreement.getInstance("DH").apply {
        init(privateKey, SecureRandom())
        doPhase(publicKey, true)
    }

    return keyAgree.generateSecret()
}

fun getHash(input: ByteArray): ByteArray {
    return MessageDigest.getInstance("SHA-256").digest(input)
}

@Throws(IllegalArgumentException::class)
fun verifyData(key: String, signature: String): Boolean {
    val verifier = Signature.getInstance("SHA256withRSA")
    val decodedSignature = Base64.decode(signature, Base64.NO_PADDING)

    // Init verifier
    with(verifier) {
        initVerify(SIGN_PUBLIC_KEY_INSTANCE)
        update(Base64.decode(key, Base64.NO_PADDING))
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
    return with(Mac.getInstance("HmacSHA256")) {
        init(SecretKeySpec(secretKey, "HmacSHA256"))
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