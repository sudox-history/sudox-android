package com.sudox.protocol

import com.sudox.protocol.helper.randomHexString
import com.sudox.protocol.model.SymmetricKey
import com.sudox.protocol.model.dto.VerifyRandomDTO
import com.sudox.protocol.model.dto.VerifySignatureDTO
import io.reactivex.Single
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

class ProtocolHandshake(private val client: ProtocolClient) {

    // Keys start

    private val SIGN_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvR9FiI9iaw9oiTFCCGwQ" +
            "Xej4Rmkg8w1pG2M+GdrtQC2pRxTFlEnO5iAHT7mqnQb29zAJbp0Jx8Z+UmypyCI9" +
            "hn0EimQLvcBTynqI1aMPMJmzemdF5vnm3GCyuWvOKqE66Z9hj+ilVqOn0KKgVq2e" +
            "j5DN1Iv9xv28+QRm70BFTowZ15ISazoGX3j7lUPFpDuiz4vfX/CbN4D8wnboqugm" +
            "I2UEuF5uRs9IGDIWZZDmpSmlEZuqAHQQA+Mvzl3P0eqMfnhV0NlA1xPcVLNdwCbP" +
            "lAI1W5zrwAG5Q5zzN+NlQeGxi92Q+NrQCTeZpL5fkRMfT0d21gNh6Qg4PjgBirSS" +
            "TQIDAQAB"

    private val PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxdLbOnUVON1LdUOLNOZs\n" +
            "qe5zH2w37xstEz4YQsLnNeV27kly89JI0V7vsWqv7b/+VqSCra5TcoJ7X+NkGM5+\n" +
            "0qcN8y+98pru5DvyUBb7LGJA1qrBBuZfXQHk13JgooTR1n4EV84JZ/AqYkVZU0Xp\n" +
            "1Li5capxOSo7BR1Tyd6M0kufmm80qOyZ+6olcO+bNRSCbPJeU1bNHwkGapeAzOf4\n" +
            "R5Q2IaQ0soE0yZ7D9nNPlLaRUnsJwCQBd/howUCdZlBXII7Zjf+vNLzJKi52Tz2k\n" +
            "qmP/9OexFNDoQPVcRK5ClrHSD7i8JVn4LfYdGO6/s4ZlkASyWm4PGbT6V4EGO0Px\n" +
            "NQIDAQAB\n" +
            "-----END PUBLIC KEY-----\n"

    // Keys end

    private fun getPublicKey(): PublicKey {
        val keyFactory = KeyFactory.getInstance("RSA")

        // Create key spec
        val x509EncodedKeySpec = X509EncodedKeySpec(SIGN_PUBLIC_KEY.toByteArray())

        // Generate public key
        return keyFactory.generatePublic(x509EncodedKeySpec)
    }

    fun verifySignature(publicKeySignature: String) {
        val signature = Signature.getInstance("SHA256withRSA")

        // Configure signature instance
        with(signature) {
            val publicKey = getPublicKey()

            // Initialize verify
            initVerify(publicKey)
            update(PUBLIC_KEY.toByteArray())
        }

        //return signature.verify()
    }

    fun execute() = Single.create<SymmetricKey> {
        client.listenMessageOnce("verify", VerifySignatureDTO::class)
                .subscribe { verifySignature ->

                }

        // Build message with salt
        val verifyRandom = VerifyRandomDTO(
                random = randomHexString(64)
        )

        // Send message with salt
        client.sendMessage("verify", verifyRandom)
    }
}