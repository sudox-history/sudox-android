package com.sudox.protocol

import com.sudox.protocol.model.SymmetricKey
import io.reactivex.Single

class ProtocolHandshake {

    // TODO: Constructor with ProtocolClient parameter

    fun execute() = Single.create<SymmetricKey> {
        TODO("Implement this")
    }
}