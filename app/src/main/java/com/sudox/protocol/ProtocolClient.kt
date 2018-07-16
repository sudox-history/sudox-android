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

    fun sendMessage(event: String, message: Any, encrypt: Boolean = true) {
        TODO("Implement this")
    }

    fun <T : Any> listenMessage(event: String, clazz: KClass<T>, decrypt: Boolean = true): Observable<T> {
        TODO("Implement this")
    }

    fun <T : Any> listenMessageOnce(event: String, clazz: KClass<T>, decrypt: Boolean = true): Single<T> {
        TODO("Implement this")
    }
}