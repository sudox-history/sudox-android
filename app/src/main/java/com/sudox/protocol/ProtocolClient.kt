package com.sudox.protocol

import com.sudox.protocol.helper.encryptAES
import com.sudox.protocol.helper.prepareDataForEncrypt
import com.sudox.protocol.model.JsonModel
import com.sudox.protocol.model.Payload
import com.sudox.protocol.model.SymmetricKey
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.socket.client.IO
import io.socket.client.Socket
import kotlin.reflect.KClass

class ProtocolClient {

    // TODO: ProtocolHandshake, ConnectionStatusSubject inject

    lateinit var symmetricKey: SymmetricKey

    private lateinit var socket: Socket

    fun connect() = Completable.create {
        val options = IO.Options()
        with(options) {
            forceNew = true
            reconnection = true
            multiplex = true
            upgrade = true
            secure = true
            timeout = 10000
            path = "/"
        }

        // Create instance of socket
        socket = IO.socket("http://api.sudox.ru", options)

        // Connect to the server
        socket.connect()

        // Get instance of protocolHandshake
        val protocolHandshake = ProtocolHandshake(this, ProtocolKeystore())

        // Start handshake
        protocolHandshake.execute()
    }

    fun sendMessage(event: String, message: JsonModel) {

        // Update iv and random
        symmetricKey.update()

        // Prepare message JSON Object
        val messageJsonObject = message.toJSON()

        // Prepare data for encrypt
        val json = prepareDataForEncrypt(symmetricKey, event, messageJsonObject.toString())

        // Encrypt payload
        val encryptedPayload = encryptAES(symmetricKey.key, symmetricKey.iv, json.payloadObject.toString())

        // Make payloadJson
        val payloadJson = Payload(encryptedPayload, symmetricKey.iv, json.hash).toJSON()

        // Send payload to the server
        socket.emit("packet", payloadJson)

        /*
         * symmetricKey.update()
         * json = SerializationHelper.prepareDataForEncrypt(symmetricKey, "huila", "test")
         * encryptedPayload = EncryptionHelper.encryptAES(symmetricKey.key, symmetricKey.iv, json.getPayload())
         *
         * Create json object (encrypted payload, iv, json.getHash)
         * */
    }

    fun sendHandshakeMessage(event: String, message: JsonModel) {

        // Prepare message JSON Object
        val messageJsonObject = message.toJSON()

        // Send handshake data to the server
        socket.emit(event, messageJsonObject)
    }

    fun <T : Any> listenMessage(event: String, clazz: KClass<T>, decrypt: Boolean = true): Observable<T> {
        TODO("Implement this")
    }

    fun <T : Any> listenMessageOnce(event: String, clazz: KClass<T>, decrypt: Boolean = true): Single<T> {
        TODO("Implement this")
    }
}