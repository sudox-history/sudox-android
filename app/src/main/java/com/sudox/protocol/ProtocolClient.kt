package com.sudox.protocol

import androidx.annotation.VisibleForTesting
import com.sudox.protocol.helper.*
import com.sudox.protocol.model.JsonModel
import com.sudox.protocol.model.MessageCallback
import com.sudox.protocol.model.Payload
import com.sudox.protocol.model.SymmetricKey
import io.reactivex.Completable
import io.reactivex.Single
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import kotlin.reflect.KClass

class ProtocolClient {

    // TODO: ConnectionStatusSubject inject

    @VisibleForTesting
    private lateinit var socket: Socket
    private lateinit var symmetricKey: SymmetricKey
    private var messagesCallbacks: LinkedHashMap<String, Pair<KClass<out JsonModel>, Any>> = LinkedHashMap()

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

                    // Start listen messages
                    startListeningInboundMessages()

                    // Notify subscribers, that socket was being connected
                    emitter.onComplete()
                }, {
                    emitter.onError(it)
                })
    }

    private fun startListeningInboundMessages() = socket.on("packet") {
        val message = it[0] as JSONObject

        // Packet data
        val iv: String? = message.optString("iv")
        val payload: String? = message.optString("payload")
        val hash: String? = message.optString("hash")

        // Защита от MITM-атак
        if (iv == null || payload == null || hash == null) {
            return@on
        }

        // Decrypt payload
        val decryptedPayload = decryptAES(symmetricKey.key, iv, payload) ?: return@on

        // Check hashes
        if (checkHashes(hash, decryptedPayload)) {
            val prepareDataForClient = prepareDataForClient(decryptedPayload)
            val pair = messagesCallbacks[prepareDataForClient.event]

            // Check, that event was being linked with callback
            if (pair != null) {
                // Get json object
                val messageObject = JSONObject(prepareDataForClient.message)

                // Convert message
                val jsonModel = (pair.first.java.newInstance()) as JsonModel
                val callback = pair.second as MessageCallback

                // Read message
                jsonModel.fromJSON(messageObject)

                // Call callback
                callback.onMessage(jsonModel)

                // Clean-up callback from list
                messagesCallbacks.remove(prepareDataForClient.event)
            }
        }
    }

    fun sendMessage(event: String, message: JsonModel) {
        // Update iv and random
        symmetricKey.update()

        // Prepare message JSON Object
        val messageJsonObject = message.toJSON()

        // Prepare data for encrypt
        val json = prepareDataForEncrypt(symmetricKey, event, messageJsonObject)

        // Encrypt payload
        val encryptedPayload = encryptAES(symmetricKey.key, symmetricKey.iv, json.payloadObject.toString())
                ?: return

        // Make payloadJson
        val payloadJson = Payload().apply {
            payload = encryptedPayload
            iv = symmetricKey.iv
            hash = json.hash
        }.toJSON()

        // Send payload to the server
        socket.emit("packet", payloadJson)
    }

    fun sendHandshakeMessage(event: String, message: JsonModel) {
        // Prepare message JSON Object
        val messageJsonObject = message.toJSON()

        // Send handshake data to the server
        socket.emit(event, messageJsonObject)
    }

    fun listenMessage(event: String, clazz: KClass<out JsonModel>, callback: MessageCallback) {
        messagesCallbacks[event] = Pair(clazz, callback)
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