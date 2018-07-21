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
import org.json.JSONObject
import kotlin.reflect.KClass

class ProtocolClient {

    // TODO: ConnectionStatusSubject inject

    private lateinit var symmetricKey: SymmetricKey
    private lateinit var socket: Socket

    fun connect(): Completable = Completable.create { emitter ->
        val options = IO.Options()
                .apply {
                    reconnection = true
                    secure = true
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
                .subscribe({
                    symmetricKey = it

                    // Notify subscribers, that socket was being connected
                    emitter.onComplete()
                }, {
                    emitter.onError(it)
                })
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
    }

    fun sendHandshakeMessage(event: String, message: JsonModel) {
        // Prepare message JSON Object
        val messageJsonObject = message.toJSON()

        // Send handshake data to the server
        socket.emit(event, messageJsonObject)
    }

    fun <T : JsonModel> listenMessage(event: String, clazz: KClass<T>): Observable<T> {
        TODO("Implement this")
    }

    fun <T : JsonModel> listenMessageOnce(event: String, clazz: KClass<T>): Observable<T> {
        TODO("Implement this")
    }

    fun <T : JsonModel> listenMessageHandshake(event: String, clazz: KClass<T>): Single<T> = Single.create { emitter ->
        socket.once(event) {
            val jsonObject = it[0] as JSONObject

            // Create instance of the model
            val modelInstance = clazz.java.newInstance()

            // Parse data
            modelInstance.fromJSON(jsonObject)

            // Return to the single
            emitter.onSuccess(modelInstance)
        }
    }
}