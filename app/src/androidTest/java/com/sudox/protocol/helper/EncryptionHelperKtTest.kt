package com.sudox.protocol.helper

import org.junit.Assert.*
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.Signature
import javax.crypto.Cipher

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

        // Configure signature instance for the create private key
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

    @Test
    fun testReadPublicKey() {
        val testInput = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvR9FiI9iaw9oiTFCCGwQ" +
                "Xej4Rmkg8w1pG2M+GdrtQC2pRxTFlEnO5iAHT7mqnQb29zAJbp0Jx8Z+UmypyCI9" +
                "hn0EimQLvcBTynqI1aMPMJmzemdF5vnm3GCyuWvOKqE66Z9hj+ilVqOn0KKgVq2e" +
                "j5DN1Iv9xv28+QRm70BFTowZ15ISazoGX3j7lUPFpDuiz4vfX/CbN4D8wnboqugm" +
                "I2UEuF5uRs9IGDIWZZDmpSmlEZuqAHQQA+Mvzl3P0eqMfnhV0NlA1xPcVLNdwCbP" +
                "lAI1W5zrwAG5Q5zzN+NlQeGxi92Q+NrQCTeZpL5fkRMfT0d21gNh6Qg4PjgBirSS" +
                "TQIDAQAB"

        // Read key body
        val publicKey = readPublicKey(testInput)

        // Assert
        assertNotNull(publicKey)
    }

    @Test
    fun testEncryptRSA() {
        // Generate private key
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")

        // Initialize generator
        with(keyPairGenerator) {
            initialize(2048)
        }

        val testInput = "Тест"

        // Key pair
        val keyPair = keyPairGenerator.genKeyPair()

        // Encode
        val encryptResult = decodeBase64(encryptRSA(keyPair.public, testInput)
                .toByteArray())

        // Cipher for decrypt
        val cipher = Cipher.getInstance("RSA/NONE/OAEPPadding")

        // Decrypt for the testing
        val decryptResult = with(cipher) {
            init(Cipher.DECRYPT_MODE, keyPair.private)

            // Decrypt with RSA
            val bytes = doFinal(encryptResult)

            // To string
            String(bytes)
        }

        // Validate
        assertEquals(decryptResult, testInput)
    }

    @Test
    fun testEncryptAES() {
        // Testable data
        val inputTest = "{\"anton\":\"Гандон\"}"
        val validResult = "Ox0D5kA4QcU3n0XdlM64wwAlsoDye59Y\n"

        // Insert into symmetric key the testable data
        val key = "261ea9378f03b93186408f2646a54fe291a06e3a5c0c10862332f693062fc108"
        val iv = "054c8d1a0c9af703b644c7ca83b9243b"

        // Encrypt to the RSA
        val encryptedData = encryptAES(key, iv, inputTest)

        // Validate the data
        assertEquals(encryptedData, validResult)
    }

    @Test
    fun testDecryptAES() {
        // Testable data
        val validResult = "{\"anton\":\"Гандон\"}"
        val inputTest = "Ox0D5kA4QcU3n0XdlM64wwAlsoDye59Y"

        // Insert into symmetric key the testable data
        val key = "261ea9378f03b93186408f2646a54fe291a06e3a5c0c10862332f693062fc108"
        val iv = "054c8d1a0c9af703b644c7ca83b9243b"

        // Encrypt to the RSA
        val decryptedData = decryptAES(key, iv, inputTest)

        // Validate the data
        assertEquals(decryptedData, validResult)
    }
}