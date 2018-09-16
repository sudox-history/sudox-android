package com.sudox.protocol

import android.os.AsyncTask
import android.util.Base64
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.models.dto.SecretDTO
import com.sudox.protocol.helpers.encryptAES
import com.sudox.protocol.helpers.getHmac
import com.sudox.protocol.helpers.randomBase64String
import com.sudox.protocol.helpers.toJsonArray
import com.sudox.protocol.model.JsonModel
import com.sudox.protocol.model.ReadCallback
import com.sudox.protocol.model.SingleLiveEvent
import com.sudox.protocol.threads.ReadThread
import com.sudox.protocol.threads.WriteThread
import kotlinx.coroutines.experimental.async
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import kotlin.reflect.KClass

class ProtocolClient @Inject constructor() {

    private val readCallbacks: ArrayList<ReadCallback<*>> = ArrayList()
    val connectionStateLiveData = SingleLiveEvent<ConnectState>()
    var id: String? = null
    var secret: String? = null

    // Variables for connection setup
    private val address by lazy { InetSocketAddress("api.sudox.ru", 5000) }
    private val handler by lazy { ProtocolHandler(this@ProtocolClient) }
    internal val socket by lazy { Socket() }

    // Others IO variables
    private val readThread by lazy { ReadThread(socket, { handler.handlePacket(it) }, { handler.handleEnd() }) }
    private val writeThread by lazy { WriteThread(socket) }

    fun connect() = AsyncTask.execute {
        try {
            socket.connect(address)

            // Start threads ...
            readThread.start()
            writeThread.start()

            // Preparing connection ...
            handler.handleStart()
        } catch (e: IOException) {
            connectionStateLiveData.postValue(ConnectState.CONNECT_ERROR)

            // Remove key & close connection
            handler.handleEnd()
        }
    }

    internal fun sendArray(array: Array<*>) {
        sendString(array.toJsonArray().toString())
    }

    private fun sendString(string: String) {
        writeThread.messagesQueue.put(string)
    }

    fun disconnect() {
        socket.close()
    }

    fun isConnected(): Boolean {
        return !socket.isClosed
    }

    fun sendMessage(event: String, message: JsonModel) {
        if (!(handler.key != null && isConnected())) return

        val iv = randomBase64String(16)
        val salt = randomBase64String(32)
        val json = message.toJSON()
        val hmac = Base64.encodeToString(getHmac(handler.key!!, event + json + salt), Base64.NO_WRAP)
        val payload = arrayOf(event, json, salt).toJsonArray().toString()
        val encryptedPayload = encryptAES(handler.key!!, iv, payload)
        val packet = arrayOf("msg", iv, encryptedPayload, hmac)

        sendArray(packet)
    }

    fun <T : JsonModel> addToCallbacks(event: String, clazz: KClass<T>, resultFunction: (T) -> (Unit), once: Boolean) {
        readCallbacks.plusAssign(ReadCallback(resultFunction, clazz, event, once))
    }

    inline fun <reified T : JsonModel> listenMessage(event: String, noinline resultFunction: (T) -> (Unit)) {
        addToCallbacks(event, T::class, resultFunction, false)
    }

    inline fun <reified T : JsonModel> listenMessageOnce(event: String, noinline resultFunction: (T) -> (Unit)) {
        addToCallbacks(event, T::class, resultFunction, true)
    }

    inline fun <reified T : JsonModel> makeRequest(event: String, message: JsonModel, noinline resultFunction: (T) -> (Unit)) {
        listenMessageOnce(event, resultFunction)
        sendMessage(event, message)
    }

    internal fun sendSecret() {
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

    internal fun notifyCallbacks(event: String, json: String) {
        val iterator = readCallbacks.iterator()

        while (iterator.hasNext()) {
            val next = iterator.next()

            // Check, that event's equals
            if (next.event != event)
                continue

            // Remove onceable callbacks
            if (next.once)
                iterator.remove()

            // Read the message
            val instance = next.clazz.java.newInstance().apply {
                this.fromJSON(org.json.JSONObject(json))
            }

            (next.resultFunction as (Any) -> (Unit))(instance)
        }
    }
}