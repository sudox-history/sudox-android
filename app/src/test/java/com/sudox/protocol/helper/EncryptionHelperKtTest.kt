package com.sudox.protocol.helper

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.Signature

class EncryptionHelperKtTest {

    @Test
    fun testVerifyData_success() {
        // Test data
        val testData = "Hello World!"
                .toByteArray()

        // Generate private key
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")

        // Initialize generator
        with(keyPairGenerator) {
            initialize(2048)
        }

        // Generate key
        val keyPair = keyPairGenerator.generateKeyPair()

        // Signature for testing
        val signature = Signature.getInstance("SHA256withRSA")

        // Configure signature instance for the create private key
        with(signature) {
            signature.initSign(keyPair.private)
            signature.update(testData)
        }

        // Sign data
        val testSignature = signature.sign()

        // Test verify data
        val result = verifyData(keyPair.public, testSignature, testData)

        // Assert
        assertTrue(result)
    }

    @Test
    fun testVerifyData_fail() {
        // Test data
        val testDataServer = "Hello World!"
                .toByteArray()

        val testDataClient = "Hello, Vladimir!"
                .toByteArray()

        // Generate private key
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")

        // Initialize generator
        with(keyPairGenerator) {
            initialize(2048)
        }

        // Generate key
        val keyPair = keyPairGenerator.generateKeyPair()

        // Signature for testing
        val signature = Signature.getInstance("SHA256withRSA")

        // Configure singature instance for the create private key
        with(signature) {
            signature.initSign(keyPair.private)
            signature.update(testDataServer)
        }

        // Sign data
        val testSignature = signature.sign()

        // Test verify data
        val result = verifyData(keyPair.public, testSignature, testDataClient)

        // Assert
        assertFalse(result)
    }
}