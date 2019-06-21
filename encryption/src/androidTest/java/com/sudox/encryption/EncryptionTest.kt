package com.sudox.encryption

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class EncryptionTest : Assert() {

    @Test
    fun testGenerateBytes() {
        val count = Random.nextInt(16, 32 * 1024)
        val firstBytes = Encryption.generateBytes(count)
        val secondBytes = Encryption.generateBytes(count)
        assertEquals(count, firstBytes.size)
        assertEquals(count, secondBytes.size)
        assertFalse(firstBytes.contentEquals(secondBytes))
    }

    @Test
    fun testCalculateHMAC() {
        val key = "012345678901234567890123".toByteArray()
        val message = "Hello Sudox!".toByteArray()
        val validHmac = Base64.decode("Yxy+iOT2cEO5mhNp5YbbIqoyvTW5XSW41+kK+Q==", Base64.NO_WRAP)
        val result = Encryption.computeHMAC(key, message)
        assertArrayEquals(validHmac, result)
    }

    @Test
    fun testVerifyHMAC_success() {
        val key = "012345678901234567890123".toByteArray()
        val message = "Hello Sudox!".toByteArray()
        val hmac = Base64.decode("Yxy+iOT2cEO5mhNp5YbbIqoyvTW5XSW41+kK+Q==", Base64.NO_WRAP)
        val result = Encryption.verifyHMAC(key, message, hmac)
        assertTrue(result)
    }

    @Test
    fun testVerifyHMAC_when_first_part_of_hmac_is_valid() {
        val key = "012345678901234567890123".toByteArray()
        val message = "Hello Sudox!".toByteArray()
        val hmac = Base64.decode("Yxy+iOT2cEO5mhNp5YbbIqoyvTW5XSW41+kK+Q==", Base64.NO_WRAP).copyOf(1)
        val result = Encryption.verifyHMAC(key, message, hmac)
        assertFalse(result)
    }

    @Test
    fun testVerifyHMAC_error() {
        val key = "012345678901234567890123".toByteArray()
        val message = "Hello Sudox!".toByteArray()
        val hmac = "Invalid HMAC".toByteArray()
        val result = Encryption.verifyHMAC(key, message, hmac)
        assertFalse(result)
    }

    @Test
    fun testEncryptWithAES_success() {
        val key = "012345678901234567890123".toByteArray()
        val iv = "0123456789012345".toByteArray()
        val message = "Hello Sudox!".toByteArray()
        val valid = Base64.decode("ZRNtKQgeESy6HZZ9", Base64.NO_WRAP)
        val result = Encryption.encryptWithAES(key, iv, message)
        assertArrayEquals(valid, result)
    }

    @Test
    fun testEncryptWithAES_invalid_key_length() {
        val key = "0123".toByteArray()
        val iv = "0123456789012345".toByteArray()
        val message = "Hello Sudox!".toByteArray()
        val result = Encryption.encryptWithAES(key, iv, message)
        assertNull(result)
    }

    @Test
    fun testEncryptWithAES_invalid_iv_length() {
        val key = "012345678901234567890123".toByteArray()
        val iv = "0123456789".toByteArray()
        val message = "Hello Sudox!".toByteArray()
        val result = Encryption.encryptWithAES(key, iv, message)
        assertNull(result)
    }

    @Test
    fun testDecryptWithAES_success() {
        val key = "012345678901234567890123".toByteArray()
        val iv = "0123456789012345".toByteArray()
        val message = Base64.decode("ZRNtKQgeESy6HZZ9", Base64.NO_WRAP)
        val valid = "Hello Sudox!".toByteArray()
        val result = Encryption.decryptWithAES(key, iv, message)
        assertArrayEquals(valid, result)
    }

    @Test
    fun testDecryptWithAES_invalid_key_length() {
        val key = "0123".toByteArray()
        val iv = "0123456789012345".toByteArray()
        val message = Base64.decode("ZRNtKQgeESy6HZZ9", Base64.NO_WRAP)
        val result = Encryption.decryptWithAES(key, iv, message)
        assertNull(result)
    }

    @Test
    fun testDecryptWithAES_invalid_iv_length() {
        val key = "012345678901234567890123".toByteArray()
        val iv = "0123456789".toByteArray()
        val message = Base64.decode("ZRNtKQgeESy6HZZ9", Base64.NO_WRAP)
        val result = Encryption.decryptWithAES(key, iv, message)
        assertNull(result)
    }

    @Test
    fun testVerifySignature_success() {
        val message = "Hello World!".toByteArray()
        val signature = Base64.decode("MGUCMQC5zE9SfVl1tSCpLbcKHxVjjYJ4YMiwRNIFgjVITxEgmCy0FA84iiVi16QUcLY2ZWs" +
                "CMBpfSec/zh1YYn6qCMIlTaUdgyyoHC6EcEpCPOIirWvce41qyMjQCseIVh1Ul7WhvA==", Base64.NO_WRAP)
        assertTrue(Encryption.verifySignature(message, signature))
    }

    @Test
    fun testVerifySignature_fail() {
        val message = "Hello Sudox!".toByteArray()
        val signature = Base64.decode("MGUCMQC5zE9SfVl1tSCpLbcKHxVjjYJ4YMiwRNIFgjVITxEgmCy0FA84iiVi16QUcLY2ZWs" +
                "CMBpfSec/zh1YYn6qCMIlTaUdgyyoHC6EcEpCPOIirWvce41qyMjQCseIVh1Ul7WhvA==", Base64.NO_WRAP)
        assertFalse(Encryption.verifySignature(message, signature))
    }

    @Test
    fun testVerifySignature_invalid_signature_format() {
        val message = "Hello Sudox!".toByteArray()
        val signature = "Invalid signature".toByteArray()
        assertFalse(Encryption.verifySignature(message, signature))
    }

    @Test
    fun testECDH_success() {
        val firstSession = Encryption.startECDH()
        val secondSession = Encryption.startECDH()
        val firstSecret = Encryption.finishECDH(firstSession.keyPairPointer, secondSession.publicKey)
        val secondSecret = Encryption.finishECDH(secondSession.keyPairPointer, firstSession.publicKey)
        assertArrayEquals(firstSecret, secondSecret)
    }

    @Test
    fun testECDH_fail() {
        val session = Encryption.startECDH()
        val secret = Encryption.finishECDH(session.keyPairPointer, ByteArray(0))
        assertNull(secret)
    }
}