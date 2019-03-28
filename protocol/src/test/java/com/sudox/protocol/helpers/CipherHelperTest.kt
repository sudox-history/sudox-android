package com.sudox.protocol.helpers

import android.util.Base64
import com.sudox.protocol.BASE64_REGEX
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.Signature
import java.security.interfaces.ECPublicKey
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

class CipherHelperTest : Assert() {

    @JvmField
    var signatureVerifier: Signature? = null

    @Before
    fun setUp() {
        // Fix ClassNotFoundException
        randomBase64String(32)

        // Load signature
        if (signatureVerifier == null) {
            signatureVerifier = Class.forName("com.sudox.protocol.helpers.CipherHelperKt")
                    .getDeclaredField("SIGNATURE_VERIFIER")
                    .apply { isAccessible = true }
                    .get(null) as Signature
        }
    }

    @Test
    fun testReadSignaturePublicKey() {
        val generator = KeyPairGenerator.getInstance("EC").apply {
            initialize(256, SecureRandom())
        }

        val pair = generator.generateKeyPair()
        val public = pair.public as ECPublicKey
        val body = Base64.encodeToString(public.encoded, Base64.NO_WRAP)
        val read = readSignaturePublicKey(body)

        // Validate ...
        assertEquals(public, read)
    }

    @Test
    fun testGenerateKeys() {
        val pair = generateKeys()
        val public = pair.public
        val private = pair.private

        // Check ...
        assertNotNull(pair)
        assertNotNull(public)
        assertNotNull(private)
    }

    @Test
    fun testGetPoint() {
        val generator = KeyPairGenerator.getInstance("EC").apply {
            initialize(384, SecureRandom())
        }

        // Testing
        val pair = generator.generateKeyPair()
        val public = pair.public as ECPublicKey
        val point = public.getPoint()
        val x = point.copyOfRange(1, 49)
        val y = point.copyOfRange(49, 97)
        val affineX = removeLeadingZeros(public.w.affineX.toByteArray())
        val affineY = removeLeadingZeros(public.w.affineY.toByteArray())

        // Validate
        assertArrayEquals(x, affineX)
        assertArrayEquals(y, affineY)
    }

    @Test
    fun testReadPublicKey() {
        val generator = KeyPairGenerator.getInstance("EC").apply {
            initialize(384, SecureRandom())
        }

        // Testing ...
        val pair = generator.generateKeyPair()
        val public = pair.public as ECPublicKey
        val point = public.getPoint()
        val body = Base64.encodeToString(point, Base64.NO_WRAP)
        val read = readPublicKey(body)

        // Verifying
        assertEquals(public, read)
    }

    @Test
    fun testVerifyData_success() {
        val data = "Hello, World!".toByteArray()
        val generator = KeyPairGenerator.getInstance("EC").apply { initialize(384, SecureRandom()) }
        val signer = Signature.getInstance("SHA224withECDSA")
        val pair = generator.generateKeyPair()

        signer.initSign(pair.private)
        signer.update(data)

        signatureVerifier!!.initVerify(pair.public)

        // Testing
        val dataBase64 = Base64.encodeToString(data, Base64.NO_WRAP)
        val signature = Base64.encodeToString(signer.sign(), Base64.NO_WRAP)
        val status = verifyData(dataBase64, signature)

        // Verifying
        assertTrue(status)
    }

    @Test
    fun testVerifyData_bad_signature() {
        val data = "Hello, World!".toByteArray()
        val generator = KeyPairGenerator.getInstance("EC").apply { initialize(384, SecureRandom()) }
        val pair = generator.generateKeyPair()

        signatureVerifier!!.initVerify(pair.public)

        // Testing
        val signature = Random.nextBytes(224)
        val dataBase64 = Base64.encodeToString(data, Base64.NO_WRAP)
        val signatureBase64 = Base64.encodeToString(signature, Base64.NO_WRAP)
        val status = verifyData(dataBase64, signatureBase64)

        // Verifying
        assertFalse(status)
    }

    @Test
    fun testVerifyData_bad_data() {
        val data = "Hello, World!".toByteArray()
        val generator = KeyPairGenerator.getInstance("EC").apply { initialize(384, SecureRandom()) }
        val signer = Signature.getInstance("SHA224withECDSA")
        val pair = generator.generateKeyPair()

        signer.initSign(pair.private)
        signer.update(data)

        signatureVerifier!!.initVerify(pair.public)

        // Testing
        val signature = Base64.encodeToString(signer.sign(), Base64.NO_WRAP)
        val dataBase64 = Base64.encodeToString("Hello, TheMax!".toByteArray(), Base64.NO_WRAP)
        val status = verifyData(dataBase64, signature)

        // Verifying
        assertFalse(status)
    }

    @Test
    fun testVerifyData_bad_data_and_signature() {
        val generator = KeyPairGenerator.getInstance("EC").apply { initialize(384, SecureRandom()) }
        val pair = generator.generateKeyPair()

        signatureVerifier!!.initVerify(pair.public)

        // Testing
        val signature = Random.nextBytes(224)
        val dataBase64 = Base64.encodeToString("Hello, TheMax!".toByteArray(), Base64.NO_WRAP)
        val signatureBase64 = Base64.encodeToString(signature, Base64.NO_WRAP)
        val status = verifyData(dataBase64, signatureBase64)

        // Verifying
        assertFalse(status)
    }

    @Test
    fun testDecryptAES() {
        val data = "Hello, World!"
        val key = Random.nextBytes(24)
        val iv = Random.nextBytes(16)
        val keySpec = SecretKeySpec(key, "AES")
        val parameterSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance("AES/CTR/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec)
        }

        val bytes = cipher.doFinal(data.toByteArray())
        val encodedBytes = Base64.encode(bytes, Base64.NO_WRAP)
        val encrypted = String(encodedBytes)

        // Testing ...
        bindEncryptionKey(key)
        val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
        val result = decryptAES(ivBase64, encrypted)

        // Verifying
        assertEquals(data, result)
    }

    @Test
    fun testEncryptAES() {
        val key = Random.nextBytes(24)
        bindEncryptionKey(key)

        val data = "Hello, World!"
        val result = encryptAES(data)
        val keySpec = SecretKeySpec(key, "AES")
        val parameterSpec = IvParameterSpec(result.first)
        val cipher = Cipher.getInstance("AES/CTR/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec)
        }

        val bytes = cipher.doFinal(data.toByteArray())
        val encodedBytes = Base64.encode(bytes, Base64.NO_WRAP)
        val encrypted = String(encodedBytes)

        // Verifying
        assertEquals(encrypted, String(result.second))
    }

    @Test
    fun testCalculateHMAC() {
        val message = "Hello, World"
        val key = Random.nextBytes(24)
        val mac = Mac.getInstance("HmacSHA224").apply {
            init(SecretKeySpec(key, "HmacSHA224"))
        }

        bindEncryptionKey(key)
        val hmac = mac.doFinal(message.toByteArray())
        val result = calculateHMAC(message)

        // Verifying
        assertArrayEquals(hmac, result)
    }

    @Test
    fun testCalculateHash() {
        val message = "Hello, World".toByteArray()
        val hash = MessageDigest.getInstance("SHA-224").digest(message)
        val result = calculateHash(message)

        // Verifying
        assertArrayEquals(hash, result)
    }

    @Test
    fun testCalculateSecretKey() {
        val generator = KeyPairGenerator.getInstance("EC").apply {
            initialize(384, SecureRandom())
        }

        // Testing ...
        val pair = generator.generateKeyPair()
        val public = pair.public
        val private = pair.private
        val secret = with(KeyAgreement.getInstance("ECDH")) {
            init(private, SecureRandom())
            doPhase(public, true)
            generateSecret()
        }

        val result = calculateSecretKey(private, public)

        // Verifying
        assertArrayEquals(secret, result)
    }

    @Test
    fun testRandomBase64String() {
        val result = randomBase64String(32)

        // Verifying
        assertEquals(result.length, 44)
        assertTrue(BASE64_REGEX.matches(result))
    }
}