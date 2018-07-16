package com.sudox.protocol

import android.util.Base64
import androidx.annotation.Nullable
import java.security.PublicKey

class ProtocolKeystore {

    // Signature public key
    private var SIGNATURE_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvR9FiI9iaw9oiTFCCGwQ\n" +
            "Xej4Rmkg8w1pG2M+GdrtQC2pRxTFlEnO5iAHT7mqnQb29zAJbp0Jx8Z+UmypyCI9\n" +
            "hn0EimQLvcBTynqI1aMPMJmzemdF5vnm3GCyuWvOKqE66Z9hj+ilVqOn0KKgVq2e\n" +
            "j5DN1Iv9xv28+QRm70BFTowZ15ISazoGX3j7lUPFpDuiz4vfX/CbN4D8wnboqugm\n" +
            "I2UEuF5uRs9IGDIWZZDmpSmlEZuqAHQQA+Mvzl3P0eqMfnhV0NlA1xPcVLNdwCbP\n" +
            "lAI1W5zrwAG5Q5zzN+NlQeGxi92Q+NrQCTeZpL5fkRMfT0d21gNh6Qg4PjgBirSS\n" +
            "TQIDAQAB\n" +
            "-----END PUBLIC KEY-----\n"

    // Public keys bodies
    private var publicKeysBodies: List<String> = ArrayList()

    init {
        publicKeysBodies.plusElement("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxdLbOnUVON1LdUOLNOZs" +
                "qe5zH2w37xstEz4YQsLnNeV27kly89JI0V7vsWqv7b/+VqSCra5TcoJ7X+NkGM5+" +
                "0qcN8y+98pru5DvyUBb7LGJA1qrBBuZfXQHk13JgooTR1n4EV84JZ/AqYkVZU0Xp" +
                "1Li5capxOSo7BR1Tyd6M0kufmm80qOyZ+6olcO+bNRSCbPJeU1bNHwkGapeAzOf4" +
                "R5Q2IaQ0soE0yZ7D9nNPlLaRUnsJwCQBd/howUCdZlBXII7Zjf+vNLzJKi52Tz2k" +
                "qmP/9OexFNDoQPVcRK5ClrHSD7i8JVn4LfYdGO6/s4ZlkASyWm4PGbT6V4EGO0Px" +
                "NQIDAQAB")

        // TODO
    }

    @Nullable
    fun findKey(signature: ByteArray): PublicKey? {
        TODO("Implement this")
    }
}