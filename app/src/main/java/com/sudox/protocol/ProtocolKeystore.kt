package com.sudox.protocol

import com.sudox.protocol.helper.decodeBase64String
import com.sudox.protocol.helper.getHashString
import com.sudox.protocol.helper.readPublicKey
import com.sudox.protocol.helper.verifyData
import java.security.PublicKey
import java.util.*

class ProtocolKeystore {

    // Signature public key body
    private var signaturePublicKeyBody = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvR9FiI9iaw9oiTFCCGwQ" +
            "Xej4Rmkg8w1pG2M+GdrtQC2pRxTFlEnO5iAHT7mqnQb29zAJbp0Jx8Z+UmypyCI9" +
            "hn0EimQLvcBTynqI1aMPMJmzemdF5vnm3GCyuWvOKqE66Z9hj+ilVqOn0KKgVq2e" +
            "j5DN1Iv9xv28+QRm70BFTowZ15ISazoGX3j7lUPFpDuiz4vfX/CbN4D8wnboqugm" +
            "I2UEuF5uRs9IGDIWZZDmpSmlEZuqAHQQA+Mvzl3P0eqMfnhV0NlA1xPcVLNdwCbP" +
            "lAI1W5zrwAG5Q5zzN+NlQeGxi92Q+NrQCTeZpL5fkRMfT0d21gNh6Qg4PjgBirSS" +
            "TQIDAQAB"

    // Signature public key instance
    private var signaturePublicKey = readPublicKey(signaturePublicKeyBody)

    // Public keys
    private var publicKeys: ArrayList<String> = ArrayList()

    init {
        publicKeys.plusAssign("-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxdLbOnUVON1LdUOLNOZs\n" +
                "qe5zH2w37xstEz4YQsLnNeV27kly89JI0V7vsWqv7b/+VqSCra5TcoJ7X+NkGM5+\n" +
                "0qcN8y+98pru5DvyUBb7LGJA1qrBBuZfXQHk13JgooTR1n4EV84JZ/AqYkVZU0Xp\n" +
                "1Li5capxOSo7BR1Tyd6M0kufmm80qOyZ+6olcO+bNRSCbPJeU1bNHwkGapeAzOf4\n" +
                "R5Q2IaQ0soE0yZ7D9nNPlLaRUnsJwCQBd/howUCdZlBXII7Zjf+vNLzJKi52Tz2k\n" +
                "qmP/9OexFNDoQPVcRK5ClrHSD7i8JVn4LfYdGO6/s4ZlkASyWm4PGbT6V4EGO0Px\n" +
                "NQIDAQAB\n" +
                "-----END PUBLIC KEY-----\n")
    }

    // Searches the key that matches the specified signature and random
    fun findKey(random: String, signature: String): PublicKey? {
        for (publicKey in publicKeys) {
            val keyHash = getHashString(publicKey)

            // Key hash salt
            val signData = random + keyHash
            val decodedSignData = signData.toByteArray()

            // Decoded signature
            val decodedSignature = decodeBase64String(signature)

            // Try verify the key
            if (verifyData(signaturePublicKey, decodedSignature, decodedSignData)) {
                val keyBody = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replace("\n", "")

                return readPublicKey(keyBody)
            }
        }

        // Key not found :(
        return null
    }
}