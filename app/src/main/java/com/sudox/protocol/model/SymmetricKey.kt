package com.sudox.protocol.model

import com.sudox.protocol.helper.randomHexString

class SymmetricKey {

    lateinit var key: String
    lateinit var iv: String
    lateinit var random: String

    fun generate() {
        key = randomHexString(64)
    }

    fun update() {
        iv = randomHexString(32)
        random = randomHexString(32)
    }
}