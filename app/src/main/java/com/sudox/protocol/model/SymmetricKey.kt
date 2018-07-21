package com.sudox.protocol.model

import com.sudox.protocol.helper.randomBase64String

class SymmetricKey {

    lateinit var key: String
    lateinit var iv: String
    lateinit var random: String

    fun generate() {
        key = randomBase64String(32)
    }

    fun update() {
        iv = randomBase64String(16)
        random = randomBase64String(32)
    }
}