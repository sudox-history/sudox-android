package com.sudox.protocol

import com.sudox.protocol.model.SymmetricKey
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlin.reflect.KClass

class ProtocolClient {

    // TODO: ProtocolEncryptorHelper, ProtocolDecryptor, ProtocolHandshake, ConnectionStatusSubject inject

    lateinit var symmetricKey: SymmetricKey

    fun connect() = Completable.create {
        TODO("Implement this")
    }

    fun sendStringMessage(event: String, message: Any, encrypt: Boolean = true) {
        TODO("Implement this")

        /**
         * symmetricKey.update()
         * json = SerializationHelper.performDataForEncrypt(symmetricKey, "huila", "test")
         * encryptedPayload = EncryptionHelper.encryptAES(symmetricKey.key, symmetricKey.iv, json.getPayload())
         *
         * Create json object (encrypted payload, iv, json.getHash)
         * **/
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