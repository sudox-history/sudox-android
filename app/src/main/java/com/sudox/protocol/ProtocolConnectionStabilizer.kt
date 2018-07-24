package com.sudox.protocol

import io.reactivex.Completable
import io.socket.client.Socket
import javax.inject.Inject


class ProtocolConnectionStabilizer @Inject constructor(private val protocolHandshake: ProtocolHandshake,
                                                       private val socket: Socket) {

    // TODO: Implement connection stabilizer

    fun connectionState(): Completable = Completable.create { emitter ->
        socket.on(Socket.EVENT_CONNECT_ERROR) {
            emitter.onError(Throwable("Connection error"))
        }

        socket.on(Socket.EVENT_CONNECT) {
            emitter.onComplete()
        }
    }
}