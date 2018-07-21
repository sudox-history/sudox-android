package com.sudox.protocol

import com.sudox.protocol.helper.encryptAES
import com.sudox.protocol.helper.prepareDataForEncrypt
import com.sudox.protocol.model.JsonModel
import com.sudox.protocol.model.Payload
import com.sudox.protocol.model.SymmetricKey
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlin.reflect.KClass

class ProtocolClient {

    // TODO: ProtocolHandshake, ConnectionStatusSubject inject

    lateinit var symmetricKey: SymmetricKey

    fun connect() = Completable.create {
        TODO("Implement this")
    }

    fun sendMessage(event: String, message: JsonModel) {

        // Update iv and random
        symmetricKey.update()

        // Prepare message JSON Object
        val messageJsonObject = message.toJSON()

        // Prepare data for encrypt
        val json = prepareDataForEncrypt(symmetricKey, event, messageJsonObject.toString())

        val encryptedPayload = encryptAES(symmetricKey.key, symmetricKey.iv, json.payloadObject.toString())

        // Make payloadJson
        val payloadJson = Payload(encryptedPayload, symmetricKey.iv, json.hash).toJSON()

        // Send payload to the server
        TODO("socket.emit(\"packet\", payloadJson)")

        /**
         * symmetricKey.update()
         * json = SerializationHelper.prepareDataForEncrypt(symmetricKey, "huila", "test")
         * encryptedPayload = EncryptionHelper.encryptAES(symmetricKey.key, symmetricKey.iv, json.getPayload())
         *
         * Create json object (encrypted payload, iv, json.getHash)
         * **/
    }

    fun sendHandshakeMessage(event: String, message: Any, encrypt: Boolean = true) {

    }

    fun <T : Any> listenMessage(event: String, clazz: KClass<T>, decrypt: Boolean = true): Observable<T> {
        TODO("Implement this")
    }

    fun <T : Any> listenMessageOnce(event: String, clazz: KClass<T>, decrypt: Boolean = true): Single<T> {
        TODO("Implement this")
    }
}