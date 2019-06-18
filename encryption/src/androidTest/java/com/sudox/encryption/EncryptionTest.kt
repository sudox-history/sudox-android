package com.sudox.encryption

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class EncryptionTest : Assert() {
    @Suppress("RegExpAnonymousGroup")
    private val base64Regex = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?\$".toRegex()

    @Test
    fun testCheckEquals_all_bytes() {
        var first = byteArrayOf(1, 1, 1)
        var second = byteArrayOf(1, 1, 1)
        assertTrue(Encryption.checkEqualsAllBytes(first, second))

        first = byteArrayOf(1, 1, 1)
        second = byteArrayOf(1, 2, 2)
        assertFalse(Encryption.checkEqualsAllBytes(first, second))

        first = byteArrayOf(1, 1)
        second = byteArrayOf(1, 1, 1)
        assertFalse(Encryption.checkEqualsAllBytes(first, second))

        first = byteArrayOf(1, 1, 1)
        second = byteArrayOf(1, 1)
        assertFalse(Encryption.checkEqualsAllBytes(first, second))
    }

    @Test
    fun testCountNonEqualityBytes() {
        var first = byteArrayOf(1, 1, 1)
        var second = byteArrayOf(1, 1, 1)
        assertEquals(0, Encryption.countNonEqualityBytes(first, second))

        first = byteArrayOf(1, 1, 1)
        second = byteArrayOf(1, 2, 2)
        assertEquals(2, Encryption.countNonEqualityBytes(first, second))
    }

    @Test
    fun testSecretKeyCalculation_fail() {
        val alicePairId = Encryption.generateKeysPair()
        val alicePrivate = Encryption.getPrivateKey(alicePairId)
        val bobPairId = Encryption.generateKeysPair()
        val bobPrivate = Encryption.getPrivateKey(bobPairId)

        // Testing ...
        val aliceSecret = Encryption.calculateSecretKey(alicePrivate!!, ByteArray(0))
        val bobSecret = Encryption.calculateSecretKey(bobPrivate!!, ByteArray(0))

        // Verifying ...
        assertNull(aliceSecret)
        assertNull(bobSecret)
    }

    @Test
    fun testSecretKeyCalculation_success() {
        val alicePairId = Encryption.generateKeysPair()
        val alicePrivate = Encryption.getPrivateKey(alicePairId)
        val alicePublic = Encryption.getPublicKey(alicePairId)
        val bobPairId = Encryption.generateKeysPair()
        val bobPrivate = Encryption.getPrivateKey(bobPairId)
        val bobPublic = Encryption.getPublicKey(bobPairId)

        // Testing ...
        val aliceSecret = Encryption.calculateSecretKey(alicePrivate!!, bobPublic!!)
        val bobSecret = Encryption.calculateSecretKey(bobPrivate!!, alicePublic!!)

        // Verifying ...
        assertArrayEquals(aliceSecret, bobSecret)
    }

    @Test
    fun testKeyGenerationSystem() {
        val pairId = Encryption.generateKeysPair()

        // Testing keys loading ...
        var private = Encryption.getPrivateKey(pairId)
        var public = Encryption.getPublicKey(pairId)

        // Verifying ...
        assertEquals(48, private!!.size)
        assertEquals(97, public!!.size)

        // Testing pairs removing ...
        Encryption.removeKeysPair(pairId)

        private = Encryption.getPrivateKey(pairId)
        public = Encryption.getPublicKey(pairId)

        // Verifying ...
        assertNull(private)
        assertNull(public)

        // Testing all pairs removing ...
        val secondPairId = Encryption.generateKeysPair()
        val thirdPairId = Encryption.generateKeysPair()

        Encryption.removeAllKeysPairs()

        val secondPrivateKey = Encryption.getPrivateKey(secondPairId)
        val secondPublicKey = Encryption.getPrivateKey(secondPairId)
        val thirdPrivateKey = Encryption.getPrivateKey(thirdPairId)
        val thirdPublicKey = Encryption.getPrivateKey(thirdPairId)

        // Verifying ...
        assertNull(secondPrivateKey)
        assertNull(secondPublicKey)
        assertNull(thirdPrivateKey)
        assertNull(thirdPublicKey)
    }

    @Test
    fun testCalculateHMAC() {
        val key = Random.nextBytes(24)
        val message = "Hello, World".toByteArray()
        val valid = with(Mac.getInstance("HmacSHA224")) {
            init(SecretKeySpec(key, "HmacSHA224"))
            doFinal(message)
        }

        // Testing ...
        val result = Encryption.calculateHMAC(key, message)

        // Verifying ...
        assertArrayEquals(valid, result)
    }

    @Test
    fun testDecryptWithAES() {
        val key = Random.nextBytes(24)
        val iv = Random.nextBytes(16)
        val message = "Hello World!".toByteArray()

        // Testing ...
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        val encrypted = with(Cipher.getInstance("AES/CTR/NoPadding")) {
            init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            doFinal(message)
        }

        val result = Encryption.decryptWithAES(key, iv, encrypted)

        // Verifying ...
        assertArrayEquals(message, result)
    }

    @Test
    fun testDecryptWithAES_invalid_key() {
        val key = Random.nextBytes(1000)
        val iv = Random.nextBytes(16)
        val message = "Hello World!".toByteArray()

        // Testing ...
        val keySpec = SecretKeySpec(Random.nextBytes(24), "AES")
        val ivSpec = IvParameterSpec(iv)
        val encrypted = with(Cipher.getInstance("AES/CTR/NoPadding")) {
            init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            doFinal(message)
        }

        val result = Encryption.decryptWithAES(key, iv, encrypted)

        // Verifying ...
        assertNull(result)
    }

    @Test
    fun testDecryptWithAES_invalid_iv() {
        val key = Random.nextBytes(24)
        val iv = Random.nextBytes(1)
        val message = "Hello World!".toByteArray()

        // Testing ...
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(Random.nextBytes(16))
        val encrypted = with(Cipher.getInstance("AES/CTR/NoPadding")) {
            init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            doFinal(message)
        }

        val result = Encryption.decryptWithAES(key, iv, encrypted)

        // Verifying ...
        assertNull(result)
    }

    @Test
    fun testEncryptWithAES() {
        val key = Random.nextBytes(24)
        val iv = Random.nextBytes(16)
        val message = "Hello World!".toByteArray()

        // Testing ...
        val result = Encryption.encryptWithAES(key, iv, message)

        // Verifying ...
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        val decrypted = with(Cipher.getInstance("AES/CTR/NoPadding")) {
            init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            doFinal(message)
        }

        assertArrayEquals(decrypted, result)
    }

    @Test
    fun testVerifyMessageWithECDSA_ieee_signature() {
        val message = "Hello World!".toByteArray()
        val signature = Base64.decode("ucxPUn1ZdbUgqS23Ch8VY42CeGDIsETSBYI1SE8RIJgstBQPOIolYte" +
                "kFHC2NmVrGl9J5z/OHVhifqoIwiVNpR2DLKgcLoRwSkI84iKta9x7jWrIyNAKx4hWHVSXtaG8", Base64.NO_WRAP)

        // Verifying ...
        assertTrue(Encryption.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testVerifyMessageWithECDSA_asn1_signature() {
        val message = "Hello World!".toByteArray()
        val signature = Base64.decode("MGUCMQC5zE9SfVl1tSCpLbcKHxVjjYJ4YMiwRNIFgjVITxEgmCy0FA84iiVi16QUcLY2ZWs" +
                "CMBpfSec/zh1YYn6qCMIlTaUdgyyoHC6EcEpCPOIirWvce41qyMjQCseIVh1Ul7WhvA==", Base64.NO_WRAP)

        // Verifying ...
        assertTrue(Encryption.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testVerifyMessageWithECDSA_asn1_signature_with_modified_data() {
        val message = "Goodbye World!".toByteArray()
        val signature = Base64.decode("MGUCMQC5zE9SfVl1tSCpLbcKHxVjjYJ4YMiwRNIFgjVITxEgmCy0FA84iiVi16QUcLY2ZWs" +
                "CMBpfSec/zh1YYn6qCMIlTaUdgyyoHC6EcEpCPOIirWvce41qyMjQCseIVh1Ul7WhvA==", Base64.NO_WRAP)

        // Verifying ...
        assertFalse(Encryption.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testVerifyMessageWithECDSA_ieee_signature_with_modified_data() {
        val message = "Goodbye World!".toByteArray()
        val signature = Base64.decode("ucxPUn1ZdbUgqS23Ch8VY42CeGDIsETSBYI1SE8RIJgstBQPOIolYte" +
                "kFHC2NmVrGl9J5z/OHVhifqoIwiVNpR2DLKgcLoRwSkI84iKta9x7jWrIyNAKx4hWHVSXtaG8", Base64.NO_WRAP)

        // Verifying ...
        assertFalse(Encryption.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testVerifyMessageWithECDSA_invalid_data_format() {
        val message = "Goodbye World!".toByteArray()
        val signature = "Test".toByteArray()

        // Verifying ...
        assertFalse(Encryption.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testCalculateSHA224() {
        val param = "Hello, World!"
        val validHash = MessageDigest.getInstance("SHA-224").digest(param.toByteArray())
        val hash = Encryption.calculateSHA224(param.toByteArray())

        // Verifying ...
        assertArrayEquals(validHash, hash)
    }

    @Test
    fun testDecodeBase64() {
        val param = "SGVsbG8sIFdvcmxkIQ=="
        val validDecoded = "Hello, World!"
        val decoded = String(Encryption.decodeFromBase64(param.toByteArray()))

        // Verifying ...
        assertEquals(validDecoded, decoded)
    }

    @Test
    fun testEncodeBase64() {
        val param = "Hello, World!"
        val validEncoded = "SGVsbG8sIFdvcmxkIQ=="
        val encoded = Encryption.encodeToBase64(param.toByteArray())

        // Verifying ...
        assertEquals(validEncoded, encoded)
        assertTrue(base64Regex.matches(encoded))
    }

    @Test
    fun testGenerateBase64() {
        val firstLength = Random.nextInt(10, 512)
        val first = Encryption.generateBase64(firstLength)
        val firstDecoded = Base64.decode(first, Base64.NO_WRAP)

        // Verifying ...
        assertEquals(firstDecoded.size, firstLength)
        assertTrue(base64Regex.matches(first))

        // Check, that results not equals ...
        val second = Encryption.generateBase64(firstLength)

        // Verifying ...
        assertFalse(first.contentEquals(second))
        assertTrue(base64Regex.matches(second))
    }

    @Test
    fun testGenerateBytes() {
        val firstLength = Random.nextInt(10, 512)
        val firstBytes = Encryption.generateBytes(firstLength)

        // Verifying ...
        assertEquals(firstLength, firstBytes.size)

        // Check, that results not equals ...
        val secondBytes = Encryption.generateBytes(firstLength)

        // Verifying ...
        assertFalse(firstBytes.contentEquals(secondBytes))
    }
}