package com.sudox.cipher

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
class CipherTest : Assert() {

    @Suppress("RegExpAnonymousGroup")
    private val base64Regex = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?\$".toRegex()

    @Test
    fun testCheckEquals_all_bytes() {
        var first = byteArrayOf(1, 1, 1)
        var second = byteArrayOf(1, 1, 1)
        assertTrue(com.sudox.cipher.Cipher.checkEqualsAllBytes(first, second))

        first = byteArrayOf(1, 1, 1)
        second = byteArrayOf(1, 2, 2)
        assertFalse(com.sudox.cipher.Cipher.checkEqualsAllBytes(first, second))

        first = byteArrayOf(1, 1)
        second = byteArrayOf(1, 1, 1)
        assertFalse(com.sudox.cipher.Cipher.checkEqualsAllBytes(first, second))

        first = byteArrayOf(1, 1, 1)
        second = byteArrayOf(1, 1)
        assertFalse(com.sudox.cipher.Cipher.checkEqualsAllBytes(first, second))
    }

    @Test
    fun testCountNonEqualityBytes() {
        var first = byteArrayOf(1, 1, 1)
        var second = byteArrayOf(1, 1, 1)
        assertEquals(0, com.sudox.cipher.Cipher.countNonEqualityBytes(first, second))

        first = byteArrayOf(1, 1, 1)
        second = byteArrayOf(1, 2, 2)
        assertEquals(2, com.sudox.cipher.Cipher.countNonEqualityBytes(first, second))
    }

    @Test
    fun testSecretKeyCalculation_fail() {
        val alicePairId = com.sudox.cipher.Cipher.generateKeysPair()
        val alicePrivate = com.sudox.cipher.Cipher.getPrivateKey(alicePairId)
        val bobPairId = com.sudox.cipher.Cipher.generateKeysPair()
        val bobPrivate = com.sudox.cipher.Cipher.getPrivateKey(bobPairId)

        // Testing ...
        val aliceSecret = com.sudox.cipher.Cipher.calculateSecretKey(alicePrivate, ByteArray(0))
        val bobSecret = com.sudox.cipher.Cipher.calculateSecretKey(bobPrivate, ByteArray(0))

        // Verifying ...
        assertEquals(0, aliceSecret.size)
        assertEquals(0, bobSecret.size)
    }

    @Test
    fun testSecretKeyCalculation_success() {
        val alicePairId = com.sudox.cipher.Cipher.generateKeysPair()
        val alicePrivate = com.sudox.cipher.Cipher.getPrivateKey(alicePairId)
        val alicePublic = com.sudox.cipher.Cipher.getPublicKey(alicePairId)
        val bobPairId = com.sudox.cipher.Cipher.generateKeysPair()
        val bobPrivate = com.sudox.cipher.Cipher.getPrivateKey(bobPairId)
        val bobPublic = com.sudox.cipher.Cipher.getPublicKey(bobPairId)

        // Testing ...
        val aliceSecret = com.sudox.cipher.Cipher.calculateSecretKey(alicePrivate, bobPublic)
        val bobSecret = com.sudox.cipher.Cipher.calculateSecretKey(bobPrivate, alicePublic)

        // Verifying ...
        assertArrayEquals(aliceSecret, bobSecret)
    }

    @Test
    fun testKeyGenerationSystem() {
        val pairId = com.sudox.cipher.Cipher.generateKeysPair()

        // Testing keys loading ...
        var private = com.sudox.cipher.Cipher.getPrivateKey(pairId)
        var public = com.sudox.cipher.Cipher.getPublicKey(pairId)

        // Verifying ...
        assertEquals(48, private.size)
        assertEquals(97, public.size)

        // Testing pairs removing ...
        com.sudox.cipher.Cipher.removeKeysPair(pairId)

        private = com.sudox.cipher.Cipher.getPrivateKey(pairId)
        public = com.sudox.cipher.Cipher.getPublicKey(pairId)

        // Verifying ...
        assertEquals(0, private.size)
        assertEquals(0, public.size)

        // Testing all pairs removing ...
        val secondPairId = com.sudox.cipher.Cipher.generateKeysPair()
        val thirdPairId = com.sudox.cipher.Cipher.generateKeysPair()

        com.sudox.cipher.Cipher.removeAllKeysPairs()

        val secondPrivateKey = com.sudox.cipher.Cipher.getPrivateKey(secondPairId)
        val secondPublicKey = com.sudox.cipher.Cipher.getPrivateKey(secondPairId)
        val thirdPrivateKey = com.sudox.cipher.Cipher.getPrivateKey(thirdPairId)
        val thirdPublicKey = com.sudox.cipher.Cipher.getPrivateKey(thirdPairId)

        // Verifying ...
        assertEquals(0, secondPrivateKey.size)
        assertEquals(0, secondPublicKey.size)
        assertEquals(0, thirdPrivateKey.size)
        assertEquals(0, thirdPublicKey.size)
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
        val result = com.sudox.cipher.Cipher.calculateHMAC(key, message)

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

        val result = com.sudox.cipher.Cipher.decryptWithAES(key, iv, encrypted)

        // Verifying ...
        assertArrayEquals(message, result)
    }

    @Test
    fun testEncryptWithAES() {
        val key = Random.nextBytes(24)
        val iv = Random.nextBytes(16)
        val message = "Hello World!".toByteArray()

        // Testing ...
        val result = com.sudox.cipher.Cipher.encryptWithAES(key, iv, message)

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
        assertTrue(com.sudox.cipher.Cipher.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testVerifyMessageWithECDSA_asn1_signature() {
        val message = "Hello World!".toByteArray()
        val signature = Base64.decode("MGUCMQC5zE9SfVl1tSCpLbcKHxVjjYJ4YMiwRNIFgjVITxEgmCy0FA84iiVi16QUcLY2ZWs" +
                "CMBpfSec/zh1YYn6qCMIlTaUdgyyoHC6EcEpCPOIirWvce41qyMjQCseIVh1Ul7WhvA==", Base64.NO_WRAP)

        // Verifying ...
        val start = System.nanoTime()
        assertTrue(com.sudox.cipher.Cipher.verifyMessageWithECDSA(message, signature))
        println("Time Sudox: ${System.nanoTime() - start}")
    }

    @Test
    fun testVerifyMessageWithECDSA_asn1_signature_with_modified_data() {
        val message = "Goodbye World!".toByteArray()
        val signature = Base64.decode("MGUCMQC5zE9SfVl1tSCpLbcKHxVjjYJ4YMiwRNIFgjVITxEgmCy0FA84iiVi16QUcLY2ZWs" +
                "CMBpfSec/zh1YYn6qCMIlTaUdgyyoHC6EcEpCPOIirWvce41qyMjQCseIVh1Ul7WhvA==", Base64.NO_WRAP)

        // Verifying ...
        assertFalse(com.sudox.cipher.Cipher.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testVerifyMessageWithECDSA_ieee_signature_with_modified_data() {
        val message = "Goodbye World!".toByteArray()
        val signature = Base64.decode("ucxPUn1ZdbUgqS23Ch8VY42CeGDIsETSBYI1SE8RIJgstBQPOIolYte" +
                "kFHC2NmVrGl9J5z/OHVhifqoIwiVNpR2DLKgcLoRwSkI84iKta9x7jWrIyNAKx4hWHVSXtaG8", Base64.NO_WRAP)

        // Verifying ...
        assertFalse(com.sudox.cipher.Cipher.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testCalculateSHA224() {
        val param = "Hello, World!"
        val validHash = MessageDigest.getInstance("SHA-224").digest(param.toByteArray())
        val hash = com.sudox.cipher.Cipher.calculateSHA224(param.toByteArray())

        // Verifying ...
        assertArrayEquals(validHash, hash)
    }

    @Test
    fun testDecodeBase64() {
        val param = "SGVsbG8sIFdvcmxkIQ=="
        val validDecoded = "Hello, World!"
        val decoded = String(com.sudox.cipher.Cipher.decodeFromBase64(param.toByteArray()))

        // Verifying ...
        assertEquals(validDecoded, decoded)
    }

    @Test
    fun testEncodeBase64() {
        val param = "Hello, World!"
        val validEncoded = "SGVsbG8sIFdvcmxkIQ=="
        val encoded = com.sudox.cipher.Cipher.encodeToBase64(param.toByteArray())

        // Verifying ...
        assertEquals(validEncoded, encoded)
        assertTrue(base64Regex.matches(encoded))
    }

    @Test
    fun testGenerateBase64() {
        val firstLength = Random.nextInt(10, 512)
        val first = com.sudox.cipher.Cipher.generateBase64(firstLength)
        val firstDecoded = Base64.decode(first, Base64.NO_WRAP)

        // Verifying ...
        assertEquals(firstDecoded.size, firstLength)
        assertTrue(base64Regex.matches(first))

        // Check, that results not equals ...
        val second = com.sudox.cipher.Cipher.generateBase64(firstLength)

        // Verifying ...
        assertFalse(first.contentEquals(second))
        assertTrue(base64Regex.matches(second))
    }

    @Test
    fun testGenerateBytes() {
        val firstLength = Random.nextInt(10, 512)
        val firstBytes = com.sudox.cipher.Cipher.generateBytes(firstLength)

        // Verifying ...
        assertEquals(firstLength, firstBytes.size)

        // Check, that results not equals ...
        val secondBytes = com.sudox.cipher.Cipher.generateBytes(firstLength)

        // Verifying ...
        assertFalse(firstBytes.contentEquals(secondBytes))
    }
}