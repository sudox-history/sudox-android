package com.sudox.protocol

import com.sudox.protocol.helper.*
import com.sudox.protocol.model.Callback
import com.sudox.protocol.model.Payload
import com.sudox.protocol.model.ResponseCallback
import com.sudox.protocol.model.SymmetricKey
import com.sudox.protocol.model.dto.JsonModel
import io.reactivex.Completable
import io.reactivex.Single
import io.socket.client.Socket
import org.json.JSONObject
import javax.inject.Inject
import kotlin.reflect.KClass

class ProtocolClient @Inject constructor(private val socket: Socket,
                                         private val handshake: ProtocolHandshake) {

    // TODO: ConnectionStatusSubject inject

    // Symmetric key for encryption
    private lateinit var symmetricKey: SymmetricKey

    // Callbacks list
    var messagesCallbacks: LinkedHashMap<String, Callback<*>> = LinkedHashMap()

    fun connect(): Completable = Completable.create {
        // Init connect state in stabilizer
        // Connect to the server
        socket.connect()
    }

    fun startHandshake(): Completable = Completable.create { emitter ->
        handshake.execute(this)
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
                val jsonModel = (pair.modelClass.java.newInstance()) as JsonModel
                val callback = pair.callback as ResponseCallback<JsonModel>

                // Read message
                jsonModel.fromJSON(messageObject)

                // Call callback
                callback.onMessage(jsonModel)

                // Clean-up callback from list
                if (pair.once) {
                    messagesCallbacks.remove(prepareDataForClient.event)
                }
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

    inline fun <reified T : JsonModel> listenMessageOnce(event: String, callback: ResponseCallback<T>) {
        messagesCallbacks[event] = Callback(T::class, callback, true)
    }

    inline fun <reified T : JsonModel> listenMessage(event: String, callback: ResponseCallback<T>) {
        messagesCallbacks[event] = Callback(T::class, callback, false)
    }

    inline fun <reified T : JsonModel> makeRequest(event: String, messageJsonModel: JsonModel, callback: ResponseCallback<T>) {
        listenMessageOnce(event, callback)

        // Send message
        sendMessage(event, messageJsonModel)
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

    fun removeCallback(event: String) {
        messagesCallbacks.remove(event)
    }

    fun disconnect() {
        socket.disconnect()
    }
}