package com.sudox.protocol.helpers

import android.support.test.runner.AndroidJUnit4
import android.util.Base64
import com.sudox.protocol.BASE64_REGEX
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
class CipherHelperTest : Assert() {

    @Test
    fun testSecretKeyCalculation_fail() {
        val alicePairId = CipherHelper.generateKeysPair()
        val alicePrivate = CipherHelper.getPrivateKey(alicePairId)
        val bobPairId = CipherHelper.generateKeysPair()
        val bobPrivate = CipherHelper.getPrivateKey(bobPairId)

        // Testing ...
        val aliceSecret = CipherHelper.calculateSecretKey(alicePrivate, ByteArray(0))
        val bobSecret = CipherHelper.calculateSecretKey(bobPrivate, ByteArray(0))

        // Verifying ...
        assertEquals(0, aliceSecret.size)
        assertEquals(0, bobSecret.size)
    }

    @Test
    fun testSecretKeyCalculation_success() {
        val alicePairId = CipherHelper.generateKeysPair()
        val alicePrivate = CipherHelper.getPrivateKey(alicePairId)
        val alicePublic = CipherHelper.getPublicKey(alicePairId)
        val bobPairId = CipherHelper.generateKeysPair()
        val bobPrivate = CipherHelper.getPrivateKey(bobPairId)
        val bobPublic = CipherHelper.getPublicKey(bobPairId)

        // Testing ...
        val aliceSecret = CipherHelper.calculateSecretKey(alicePrivate, bobPublic)
        val bobSecret = CipherHelper.calculateSecretKey(bobPrivate, alicePublic)

        // Verifying ...
        assertArrayEquals(aliceSecret, bobSecret)
    }

    @Test
    fun testKeyGenerationSystem() {
        val pairId = CipherHelper.generateKeysPair()

        // Testing keys loading ...
        var private = CipherHelper.getPrivateKey(pairId)
        var public = CipherHelper.getPublicKey(pairId)

        // Verifying ...
        assertEquals(48, private.size)
        assertEquals(97, public.size)

        // Testing pairs removing ...
        CipherHelper.removeKeysPair(pairId)

        private = CipherHelper.getPrivateKey(pairId)
        public = CipherHelper.getPublicKey(pairId)

        // Verifying ...
        assertEquals(0, private.size)
        assertEquals(0, public.size)

        // Testing all pairs removing ...
        val secondPairId = CipherHelper.generateKeysPair()
        val thirdPairId = CipherHelper.generateKeysPair()

        CipherHelper.removeAllKeysPairs()

        val secondPrivateKey = CipherHelper.getPrivateKey(secondPairId)
        val secondPublicKey = CipherHelper.getPrivateKey(secondPairId)
        val thirdPrivateKey = CipherHelper.getPrivateKey(thirdPairId)
        val thirdPublicKey = CipherHelper.getPrivateKey(thirdPairId)

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
        val result = CipherHelper.calculateHMAC(key, message)

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

        val result = CipherHelper.decryptWithAES(key, iv, encrypted)

        // Verifying ...
        assertArrayEquals(message, result)
    }

    @Test
    fun testEncryptWithAES() {
        val key = Random.nextBytes(24)
        val iv = Random.nextBytes(16)
        val message = "Hello World!".toByteArray()

        // Testing ...
        val result = CipherHelper.encryptWithAES(key, iv, message)

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
        assertTrue(CipherHelper.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testVerifyMessageWithECDSA_asn1_signature() {
        val message = "Hello World!".toByteArray()
        val signature = Base64.decode("MGUCMQC5zE9SfVl1tSCpLbcKHxVjjYJ4YMiwRNIFgjVITxEgmCy0FA84iiVi16QUcLY2ZWs" +
                "CMBpfSec/zh1YYn6qCMIlTaUdgyyoHC6EcEpCPOIirWvce41qyMjQCseIVh1Ul7WhvA==", Base64.NO_WRAP)

        // Verifying ...
        assertTrue(CipherHelper.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testVerifyMessageWithECDSA_asn1_signature_with_modified_data() {
        val message = "Goodbye World!".toByteArray()
        val signature = Base64.decode("MGUCMQC5zE9SfVl1tSCpLbcKHxVjjYJ4YMiwRNIFgjVITxEgmCy0FA84iiVi16QUcLY2ZWs" +
                "CMBpfSec/zh1YYn6qCMIlTaUdgyyoHC6EcEpCPOIirWvce41qyMjQCseIVh1Ul7WhvA==", Base64.NO_WRAP)

        // Verifying ...
        assertFalse(CipherHelper.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testVerifyMessageWithECDSA_ieee_signature_with_modified_data() {
        val message = "Goodbye World!".toByteArray()
        val signature = Base64.decode("ucxPUn1ZdbUgqS23Ch8VY42CeGDIsETSBYI1SE8RIJgstBQPOIolYte" +
                "kFHC2NmVrGl9J5z/OHVhifqoIwiVNpR2DLKgcLoRwSkI84iKta9x7jWrIyNAKx4hWHVSXtaG8", Base64.NO_WRAP)

        // Verifying ...
        assertFalse(CipherHelper.verifyMessageWithECDSA(message, signature))
    }

    @Test
    fun testCalculateSHA224() {
        val param = "Hello, World!"
        val validHash = MessageDigest.getInstance("SHA-224").digest(param.toByteArray())
        val hash = CipherHelper.calculateSHA224(param.toByteArray())

        // Verifying ...
        assertArrayEquals(validHash, hash)
    }

    @Test
    fun testDecodeBase64() {
        val param = "SGVsbG8sIFdvcmxkIQ=="
        val validDecoded = "Hello, World!"
        val decoded = String(CipherHelper.decodeFromBase64(param.toByteArray()))

        // Verifying ...
        assertEquals(validDecoded, decoded)
    }

    @Test
    fun testEncodeBase64() {
        val param = "Hello, World!"
        val validEncoded = "SGVsbG8sIFdvcmxkIQ=="
        val encoded = CipherHelper.encodeToBase64(param.toByteArray())

        // Verifying ...
        assertEquals(validEncoded, encoded)
        assertTrue(BASE64_REGEX.matches(encoded))
    }

    @Test
    fun testGenerateBase64() {
        val firstLength = Random.nextInt(10, 512)
        val first = CipherHelper.generateBase64(firstLength)
        val firstDecoded = Base64.decode(first, Base64.NO_WRAP)

        // Verifying ...
        assertEquals(firstDecoded.size, firstLength)
        assertTrue(BASE64_REGEX.matches(first))

        // Check, that results not equals ...
        val second = CipherHelper.generateBase64(firstLength)

        // Verifying ...
        assertFalse(first!!.contentEquals(second))
        assertTrue(BASE64_REGEX.matches(second))
    }

    @Test
    fun testGenerateBytes() {
        val firstLength = Random.nextInt(10, 512)
        val firstBytes = CipherHelper.generateBytes(firstLength)

        // Verifying ...
        assertEquals(firstLength, firstBytes.size)

        // Check, that results not equals ...
        val secondBytes = CipherHelper.generateBytes(firstLength)

        // Verifying ...
        assertFalse(firstBytes!!.contentEquals(secondBytes))
    }
}