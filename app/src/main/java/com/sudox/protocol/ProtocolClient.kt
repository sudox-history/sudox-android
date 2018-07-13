package com.sudox.protocol

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlin.reflect.KClass

class ProtocolClient {

    // TODO: ProtocolEncryptor, ProtocolDecryptor, ProtocolHandshake, ConnectionStatusSubject inject

    fun connect() = Completable.create {
        TODO("Implement this")
    }

    fun sendMessage() {
        TODO("Implement this")
    }

    fun <T : Any> listenMessage(event: String, clazz: KClass<T>): Observable<T> {
        TODO("Implement this")
    }

    fun <T : Any> listenMessageOnce(event: String, clazz: KClass<T>): Single<T> {
        TODO("Implement this")
    }
}