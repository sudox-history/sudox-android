package com.sudox.protocol

import android.util.Base64
import com.sudox.android.common.enums.ConnectState
import com.sudox.android.common.models.auth.dto.AuthImportDTO
import com.sudox.protocol.helpers.encryptAES
import com.sudox.protocol.helpers.getHmac
import com.sudox.protocol.helpers.randomBase64String
import com.sudox.protocol.helpers.toJsonArray
import com.sudox.protocol.models.JsonModel
import com.sudox.protocol.models.ReadCallback
import com.sudox.protocol.models.SingleLiveEvent
import com.sudox.protocol.threads.ReadThread
import com.sudox.protocol.threads.WriteThread
import kotlinx.coroutines.experimental.async
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class ProtocolClient @Inject constructor() {

    internal val readCallbacks: ArrayList<ReadCallback<*>> = ArrayList()
    val connectionStateLiveData = SingleLiveEvent<ConnectState>()

    // Account session data ...
    var id: String? = null
    var secret: String? = null

    // Variables for connection setup
    private val address by lazy { InetSocketAddress("api.sudox.ru", 80) }
    private val handler by lazy { ProtocolHandler(this@ProtocolClient) }
    lateinit var socket: Socket

    // Others IO variables
    private var readThread: ReadThread? = null
    private var writeThread: WriteThread? = null

    fun connect() = async {
        socket = Socket()

        try {
            socket.connect(address)

            // Start threads ...
            startThreads()

            // Preparing connection ...
            handler.handleStart()
        } catch (e: IOException) {
            connectionStateLiveData.postValue(ConnectState.CONNECT_ERROR)

            // Remove key & close connection
            handler.handleEnd()
        }
    }

    fun startThreads() {
        if (readThread != null) readThread!!.interrupt()
        if (writeThread != null) writeThread!!.interrupt()

        readThread = ReadThread(socket, { handler.handlePacket(it) }, { handler.handleEnd() })
        writeThread = WriteThread(socket)

        readThread!!.start()
        writeThread!!.start()
    }

    fun refresh() {

    }

    internal fun sendArray(array: Array<*>) {
        sendString(array.toJsonArray().toString())
    }

    private fun sendString(string: String) {
        writeThread?.messagesQueue?.put(string)
    }

    fun close() = async {
        socket.close()
    }

    fun isConnected(): Boolean {
        return !socket.isClosed
    }

    fun sendMessage(event: String, message: JsonModel) {
        if (!(handler.key != null && isConnected())) {
            connectionStateLiveData.postValue(ConnectState.DISCONNECTED)
            return
        }

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
        val id = id
        val secret = secret

        // Все-таки работаем в многопоточной среде, значения могут поменяться пока мы тут возимся
        if (id == null || secret == null) {
            connectionStateLiveData.postValue(ConnectState.MISSING_TOKEN)
            return
        }

        // Пробуем установить сессию
        makeRequest<AuthImportDTO>("auth.importAuth", AuthImportDTO().apply {
            this.id = id
            this.secret = secret
        }) {
            if (it.isSuccess()) {
                connectionStateLiveData.postValue(ConnectState.CORRECT_TOKEN)
            } else {
                connectionStateLiveData.postValue(ConnectState.WRONG_TOKEN)
            }
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
                this.readFromJSON(org.json.JSONObject(json))
            }

            (next.resultFunction as (JsonModel) -> (Unit))(instance)
        }
    }
}