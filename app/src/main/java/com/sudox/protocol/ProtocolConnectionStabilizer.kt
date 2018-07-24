package com.sudox.protocol

import io.reactivex.Completable
import io.socket.client.Socket
import javax.inject.Inject

class ProtocolConnectionStabilizer @Inject constructor() {

    fun connectionState(socket: Socket): Completable = Completable.create { emitter ->
        socket.on(Socket.EVENT_CONNECT_ERROR) {
            emitter.onError(Throwable("Connection error"))
        }

        socket.on(Socket.EVENT_CONNECT) {
            emitter.onComplete()
        }
    }
}