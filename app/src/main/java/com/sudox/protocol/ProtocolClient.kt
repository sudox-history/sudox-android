package com.sudox.protocol

import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.models.dto.SecretDTO
import com.sudox.protocol.helper.*
import com.sudox.protocol.model.Callback
import com.sudox.protocol.model.Payload
import com.sudox.protocol.model.SingleLiveEvent
import com.sudox.protocol.model.SymmetricKey
import com.sudox.protocol.model.dto.JsonModel
import io.socket.client.Socket
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class ProtocolClient @Inject constructor(private val socket: Socket,
                                         private val handshake: ProtocolHandshake) {

    // Symmetric key for encryption
    private lateinit var symmetricKey: SymmetricKey

    // Callbacks list
    val callbacks: LinkedHashMap<String, Callback<*>> = LinkedHashMap()
    val connectionStateLiveData: MutableLiveData<ConnectState> = SingleLiveEvent()
    var id: String? = null
    var secret: String? = null

    fun connect() {
        registerListeners()
        socket.connect()
    }

    private fun registerListeners() {
        socket.once(Socket.EVENT_CONNECT) {
            startHandshake(false)
            startListeningInboundMessages()
        }

        socket.once(Socket.EVENT_CONNECT_ERROR) {
            socket.off(Socket.EVENT_CONNECT)
            connectionStateLiveData.postValue(ConnectState.CONNECT_ERROR)
        }

        socket.on(Socket.EVENT_RECONNECT) {
            startHandshake(true)
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            connectionStateLiveData.postValue(ConnectState.DISCONNECTED)
        }
    }

    private fun sendSecret() {
        if (secret != null && id != null) {
            makeRequest<SecretDTO>("auth.import", SecretDTO().apply {
                this.secret = this@ProtocolClient.secret!!
                this.sendId = this@ProtocolClient.id!!
            }) {
                if (it.status == 1) {
                    connectionStateLiveData.postValue(ConnectState.CORRECT_TOKEN)
                } else {
                    connectionStateLiveData.postValue(ConnectState.WRONG_TOKEN)
                }
            }
        } else {
            connectionStateLiveData.postValue(ConnectState.MISSING_TOKEN)
        }
    }

    private fun startHandshake(reconnect: Boolean) {
        var errors = 0
        val handler = Handler(Looper.getMainLooper())
        val successCallback: (SymmetricKey) -> (Unit) = {
            symmetricKey = it

            // Notify subscribers, that socket was being connected
            if (reconnect) {
                connectionStateLiveData.postValue(ConnectState.RECONNECTED)
            } else {
                connectionStateLiveData.postValue(ConnectState.CONNECTED)
            }

            sendSecret()
        }

        handshake.start(successCallback, object : (() -> Unit) {
            override fun invoke() {
                errors++

                // Check errors count
                if (errors <= 5) {
                    handler.postDelayed({
                        handshake.start(successCallback, this, this@ProtocolClient)
                    }, 5000L)
                } else {
                    notifyConnectionAttacked()
                }
            }
        }, this)
    }

    fun isConnected() = socket.connected()

    private fun notifyConnectionAttacked() {
        connectionStateLiveData.postValue(ConnectState.ATTACKED)

        // Remove protocol connection listener of disconnection
        socket.off(Socket.EVENT_DISCONNECT)

        // Close connection
        socket.disconnect()
    }

    private fun startListeningInboundMessages() = socket.on("packet") {
        val message = it[0] as JSONObject

        // Packet data
        val iv: String? = message.optString("iv")
        val payload: String? = message.optString("payload")
        val hash: String? = message.optString("hash")

        // Защита от MITM-атак
        if (iv != null && payload != null && hash != null) {
            try {
                val decryptedPayload = decryptAES(symmetricKey.key, iv, payload)

                // Защита от MITM-атаки
                if (checkHashes(hash, decryptedPayload)) {
                    val prepareDataForClient = prepareDataForClient(decryptedPayload)
                    val pair = callbacks[prepareDataForClient.event]

                    // Check, that event was being linked with callback
                    if (pair != null) {
                        // Get json object
                        val messageObject = JSONObject(prepareDataForClient.message)

                        // Convert message
                        val jsonModel = (pair.modelClass.java.newInstance()) as JsonModel
                        val callback = pair.callback as ((JsonModel) -> (Unit))

                        // Read message
                        jsonModel.fromJSON(messageObject)

                        // Call callback
                        callback(jsonModel)

                        // Clean-up callback from list
                        if (pair.once) {
                            callbacks.remove(prepareDataForClient.event)
                        }
                    }
                }
            } catch (e: Exception) {
                println(e)
                // Ignore
            }
        }
    }

    fun sendMessage(event: String, message: JsonModel) {
        if (isConnected()) {
            // Update iv and random
            symmetricKey.update()

            // Prepare message JSON Object
            val messageJsonObject = message.toJSON()

            // Prepare data for encrypt
            val json = prepareDataForEncrypt(symmetricKey, event, messageJsonObject)

            try {
                // Encrypt payload
                val encryptedPayload = encryptAES(symmetricKey.key, symmetricKey.iv, json.payloadObject.toString())

                // Make payloadJson
                val payloadJson = Payload().apply {
                    payload = encryptedPayload
                    iv = symmetricKey.iv
                    hash = json.hash
                }.toJSON()

                // Send payload to the server
                socket.emit("packet", payloadJson)
            } catch (e: Exception) {
                // Ignore
            }
        } else {
            connectionStateLiveData.postValue(ConnectState.DISCONNECTED)
        }
    }

    fun sendHandshakeMessage(event: String, message: JsonModel) {
        // Prepare message JSON Object
        val messageJsonObject = message.toJSON()

        // Send handshake data to the server
        socket.emit(event, messageJsonObject)
    }

    fun <T : JsonModel> listenMessageHandshake(event: String, clazz: KClass<T>, callback: (T) -> (Unit)) {
        socket.once(event) {
            val jsonObject = it[0] as JSONObject

            // Create instance of the model
            val modelInstance = clazz.java.newInstance()

            // Parse data
            modelInstance.fromJSON(jsonObject)

            // Return to the single
            callback.invoke(modelInstance)
        }
    }

    inline fun <reified T : JsonModel> listenMessageOnce(event: String, noinline callback: ((T) -> Unit)) {
        callbacks[event] = Callback(T::class, callback, true)
    }

    inline fun <reified T : JsonModel> listenMessage(event: String, noinline callback: ((T) -> Unit)) {
        callbacks[event] = Callback(T::class, callback, false)
    }

    inline fun <reified T : JsonModel> makeRequest(event: String, messageJsonModel: JsonModel, noinline callback: ((T) -> Unit)) {
        listenMessageOnce(event, callback)

        // Send message
        sendMessage(event, messageJsonModel)
    }

    fun removeCallback(event: String) {
        callbacks.remove(event)
    }

    fun disconnect() {
        socket.disconnect()
    }
}