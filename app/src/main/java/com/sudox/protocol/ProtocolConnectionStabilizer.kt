package com.sudox.protocol

import com.sudox.protocol.model.ConnectionStateCallback
import io.reactivex.Completable
import io.socket.client.Socket
import java.lang.reflect.Method
import javax.inject.Inject

internal const val CONNECT_TAG = 1
internal const val RECONNECT_TAG = 2

class ProtocolConnectionStabilizer @Inject constructor() {


    private val observersList: ArrayList<ConnectionStateCallback> = ArrayList()

    fun connectionState(socket: Socket): Completable = Completable.create { emitter ->
        socket.on(Socket.EVENT_CONNECT_ERROR) {
            emitter.onError(Throwable("Connection error"))
        }

        socket.once(Socket.EVENT_CONNECT) {
            emitter.onComplete()
            notifyObservers(CONNECT_TAG)
        }

        socket.on(Socket.EVENT_RECONNECT) {
            notifyObservers(RECONNECT_TAG)
        }
    }

    fun subscribe(callback: ConnectionStateCallback){
        if(!observersList.contains(callback)) observersList.add(callback)
    }

    fun unsubscribe(callback: ConnectionStateCallback){
        if(observersList.contains(callback)) observersList.remove(callback)
    }

    private fun notifyObservers(tag: Int) {
        if (tag == CONNECT_TAG)
            for (observer in observersList) {
                observer.onConnect()
            }
        else if (tag == RECONNECT_TAG)
            for (observer in observersList) {
                observer.onReconnect()
            }
    }
}