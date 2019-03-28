package com.sudox.protocol.helpers

import android.util.Base64
import com.sudox.protocol.ELLIPTIC_CURVE_PARAM
import com.sudox.protocol.SIGN_PUBLIC_KEY
import com.sudox.protocol.abstractions.MutableIVParameterSpec
import java.io.File
import java.math.BigInteger
import java.security.*
import java.security.interfaces.ECPublicKey
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val IV_PARAMETER_SPEC = MutableIVParameterSpec(16)
private val DECRYPT_CIPHER = Cipher.getInstance("AES/CTR/NoPadding")
private val ENCRYPT_CIPHER = Cipher.getInstance("AES/CTR/NoPadding")
private val KEY_AGREEMENT = KeyAgreement.getInstance("ECDH")
private var SECRET_KEY_SPEC: SecretKeySpec? = null
private val HMAC_CALCULATOR = Mac.getInstance("HmacSHA224")
private val KEY_FACTORY = KeyFactory.getInstance("EC")
private val HASH_CALCULATOR = MessageDigest.getInstance("SHA-224")
private val KEY_PAIR_GENERATOR = KeyPairGenerator.getInstance("EC").apply {
    initialize(ELLIPTIC_CURVE_PARAM, SECURE_RANDOM)
}

private val SIGNATURE_VERIFIER = Signature.getInstance("SHA224withECDSA").apply {
    initVerify(SIGN_PUBLIC_KEY)
}

private val SECURE_RANDOM = SecureRandom().apply {
    try {
        val stream = File("/dev/urandom").inputStream()
        val buffer = ByteArray(1024)

        // Read and set seed
        stream.read(buffer)
        stream.close()
        setSeed(buffer)
    } catch (e: Exception) {
        // Ignore
    }
}

/**
 * Читает публичный ключ подписи в формате ECDSA.
 *
 * @param keyBody - тело публичного ключа подписи (часть ключа без шапки)
 */
fun readSignaturePublicKey(keyBody: String): PublicKey {
    val decoded = Base64.decode(keyBody, Base64.NO_PADDING)
    val spec = X509EncodedKeySpec(decoded)

    // Generate public keySpec ...
    return KEY_FACTORY.generatePublic(spec)
}

/**
 * Читает публичный ключ в формате ECDH без сжатия, длиной 384 бита.
 *
 * @param keyBody - закодированные в Base64 байты ключа.
 */
fun readPublicKey(keyBody: String): ECPublicKey {
    val decoded = Base64.decode(keyBody, Base64.NO_PADDING)
    val keyFactory = KeyFactory.getInstance("EC")
    val x = ByteArray(49)
    val y = ByteArray(49)
    System.arraycopy(decoded, 1, x, 1, 48)
    System.arraycopy(decoded, 49, y, 1, 48)
    val xInt = BigInteger(x)
    val yInt = BigInteger(y)
    val point = ECPoint(xInt, yInt)
    val spec = ECPublicKeySpec(point, ELLIPTIC_CURVE_PARAM)

    // Generate key
    return keyFactory.generatePublic(spec) as ECPublicKey
}

/**
 * Генерирует пару ключей на основе элиптической кривой EC - P384
 */
fun generateKeys() = KEY_PAIR_GENERATOR.genKeyPair()!!

/**
 * Преобразовывает публичный EC-ключ в массив байтов без сжатия.
 */
fun ECPublicKey.getPoint(): ByteArray {
    val affineXBytes = removeLeadingZeros(w.affineX.toByteArray())
    val affineYBytes = removeLeadingZeros(w.affineY.toByteArray())
    val encodedBytes = ByteArray(48 * 2 + 1)
    encodedBytes[0] = 0x04 //uncompressed
    System.arraycopy(affineXBytes, 0, encodedBytes, 48 - affineXBytes.size + 1, affineXBytes.size)
    System.arraycopy(affineYBytes, 0, encodedBytes, encodedBytes.size - affineYBytes.size, affineYBytes.size)
    return encodedBytes
}

/**
 * Проверяет данные на соответствие публичного ключа подписи.
 *
 * @param encodedData - данные, зашифрованные в Base64
 * @param encodedSignature - подпись, зашифрованная в Base64
 */
@Throws(IllegalArgumentException::class)
fun verifyData(encodedData: String, encodedSignature: String): Boolean {
    val decodedSignature = Base64.decode(encodedSignature, Base64.NO_PADDING)
    val decodedKey = Base64.decode(encodedData, Base64.NO_PADDING)

    return try {
        SIGNATURE_VERIFIER.update(decodedKey)
        SIGNATURE_VERIFIER.verify(decodedSignature)
    } catch (e: SignatureException) {
        false
    }
}

/**
 * Расшифровывает данные, зашифрованные с помощью AES.
 *
 * @param encodedIv - IV, закодированный в Base64
 * @param encodedData - сообщение, закодированное в Base64
 */
fun decryptAES(encodedIv: String, encodedData: String): String {
    IV_PARAMETER_SPEC.iv = Base64.decode(encodedIv, Base64.NO_PADDING)
    DECRYPT_CIPHER.init(Cipher.DECRYPT_MODE, SECRET_KEY_SPEC, IV_PARAMETER_SPEC, SECURE_RANDOM)

    // Decode & decrypt
    val data = Base64.decode(encodedData, Base64.NO_PADDING)
    val decrypted = DECRYPT_CIPHER.doFinal(data)

    // Return as string
    return String(decrypted).replace("\\/", "/")
}

/**
 * Зашифровывает строку с помощью AES и возвращает пару из IV и закодированных в Base64 байтов.
 *
 * @param originalData - незашифрованная информация.
 */
@Throws(IllegalArgumentException::class, InvalidKeyException::class, IllegalBlockSizeException::class)
fun encryptAES(originalData: String): Pair<ByteArray, ByteArray> {
    val bytes = ENCRYPT_CIPHER.doFinal(originalData.toByteArray())
    val iv = ENCRYPT_CIPHER.iv
    val encoded = Base64.encode(bytes, Base64.NO_WRAP)

    // Return pair - iv & encoded to Base64 encrypted bytes
    return Pair(iv, encoded)
}

/**
 * Вычисляет HMAC строки на основе секретного ключа.
 *
 * @param message - строка, HMAC которой нужно вычислить.
 */
fun calculateHMAC(message: String): ByteArray = HMAC_CALCULATOR.doFinal(message.toByteArray())

/**
 * Вычисляет хэш переданного массива байтов.
 *
 * @param input - массив байтов, хэш которого нужны вычислить.
 */
fun calculateHash(input: ByteArray): ByteArray = HASH_CALCULATOR.digest(input)

/**
 * Вычисляет секретный ключ на основе связки публичных и приватных ключей с помощью ECDH.
 *
 * @param privateKey - приватный EC-ключ
 * @param publicKey - публичный EC-ключ.
 */
fun calculateSecretKey(privateKey: PrivateKey, publicKey: PublicKey): ByteArray {
    KEY_AGREEMENT.init(privateKey, SECURE_RANDOM)
    KEY_AGREEMENT.doPhase(publicKey, true)

    // Calculating the secret key
    return KEY_AGREEMENT.generateSecret()
}

/**
 * Генерирует случайную последовательность байтов указанной длины и кодирует в Base64.
 *
 * @param length - количество байтов для кодирования.
 */
fun randomBase64String(length: Int): String {
    val bytes = ByteArray(length)

    // Генерируем рандомные байты
    SECURE_RANDOM.nextBytes(bytes)

    // Переводим в Base-64
    return Base64
            .encodeToString(bytes, Base64.NO_WRAP)
            .replace("\n", "")
}

/**
 * Инициализирует шифрование.
 *
 * @param - секретный ключ.
 */
fun bindEncryptionKey(secretKey: ByteArray) {
    SECRET_KEY_SPEC = SecretKeySpec(secretKey, "AES")

    // Init keys ...
    ENCRYPT_CIPHER.init(Cipher.ENCRYPT_MODE, SECRET_KEY_SPEC, SECURE_RANDOM)
    HMAC_CALCULATOR.init(SECRET_KEY_SPEC)
}
