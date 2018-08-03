package com.sudox.protocol

import com.sudox.android.common.enums.ConnectState
import io.reactivex.Completable
import io.reactivex.subjects.PublishSubject
import io.socket.client.Socket
import java.io.IOException
import javax.inject.Inject

class ProtocolConnectionStabilizer @Inject constructor() {

    // Connection state live data
    var connectionLiveData: PublishSubject<ConnectState> = PublishSubject.create()

    fun connectionState(socket: Socket): Completable = Completable.create { emitter ->
        socket.once(Socket.EVENT_CONNECT) {
            emitter.onComplete()

            // Notify observers
            connectionLiveData.onNext(ConnectState.CONNECT)
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) {
            emitter.onError(IOException())

            // Notify observers
            connectionLiveData.onNext(ConnectState.ERROR)
        }

        socket.on(Socket.EVENT_RECONNECT) {
            connectionLiveData.onNext(ConnectState.RECONNECT)
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            connectionLiveData.onNext(ConnectState.DISCONNECT)
        }
    }
}