package com.sudox.protocol

import com.sudox.android.common.enums.ConnectState
import io.reactivex.Completable
import io.reactivex.subjects.PublishSubject
import io.socket.client.Socket
import javax.inject.Inject

class ProtocolConnectionStabilizer @Inject constructor(private val protocolClient: ProtocolClient) {

    // Connection state live data
    var connectionRXData: PublishSubject<ConnectState> = PublishSubject.create()

    fun connectionState(socket: Socket): Completable = Completable.create { emitter ->
        socket.once(Socket.EVENT_CONNECT) {
            emitter.onComplete()
            socket.off(Socket.EVENT_CONNECT_ERROR)

            // Notify observers
            protocolClient.startHandshake().subscribe(::startHandshake)
        }

        socket.once(Socket.EVENT_CONNECT_ERROR) {

            // Notify observers
            connectionRXData.onNext(ConnectState.ERROR)
        }

        socket.on(Socket.EVENT_RECONNECT) {
            protocolClient.startHandshake().subscribe(::startHandshake)
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            connectionRXData.onNext(ConnectState.DISCONNECT)
        }
    }


    private fun startHandshake(){
        protocolClient.startHandshake().subscribe({
            connectionRXData.onNext(ConnectState.CONNECT)
        },{
            connectionRXData.onNext(ConnectState.FAILED_HANDSHAKE)
            protocolClient.startHandshake()
        })
    }
}