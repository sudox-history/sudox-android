package com.sudox.protocol

import android.util.Base64
import com.sudox.protocol.models.enums.ConnectionState
import com.sudox.protocol.helpers.encryptAES
import com.sudox.protocol.helpers.getHmac
import com.sudox.protocol.helpers.randomBase64String
import com.sudox.protocol.helpers.toJsonArray
import com.sudox.protocol.models.JsonModel
import com.sudox.protocol.models.ReadCallback
import com.sudox.protocol.models.SingleLiveEvent
import com.sudox.protocol.threads.HandlerThread
import com.sudox.protocol.threads.ReadThread
import com.sudox.protocol.threads.WriteThread
import kotlinx.coroutines.experimental.async
import org.json.JSONObject
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates
import kotlin.reflect.KClass

@Singleton
class ProtocolClient @Inject constructor() {

    var socket: Socket? = null
    val readCallbacks = ArrayList<ReadCallback<*>>()
    val errorsMessagesCallbacks = ArrayList<(Int) -> (Unit)>()
    val connectionStateLiveData = SingleLiveEvent<ConnectionState>()

    // Потоки
    var readThread: ReadThread? = null
    var writeThread: WriteThread? = null
    var handlerThread: HandlerThread? = null

    fun connect(reconnect: Boolean = false) = async {
        try {
            if (handlerThread == null) {
                handlerThread = HandlerThread(this@ProtocolClient)
                handlerThread!!.start()
            }

            socket = Socket().apply { keepAlive = false }
            socket!!.connect(InetSocketAddress("api.sudox.ru", 5000))

            // Круто, надо бы потоки запустить ...
            startThreads()

            // Кинем в шину событий ...
            handlerThread!!.handleStart()
        } catch (e: IOException) {
            // Notify subscribers ...
            if (!reconnect) connectionStateLiveData.postValue(ConnectionState.CONNECT_ERRORED)

            // Handl end.
            handlerThread!!.handleEnd(false, reconnect)
        }
    }

    /**
     * Запускает потоки записи/чтения ...
     **/
    fun startThreads() {
        if (writeThread != null && writeThread!!.isAlive) writeThread!!.interrupt()
        if (readThread != null && readThread!!.isAlive) readThread!!.interrupt()

        readThread = ReadThread(socket!!, { handlerThread!!.handlePacket(it) }, { handlerThread!!.handleEnd(false, false) })
        writeThread = WriteThread(socket!!)

        // Start threads ...
        readThread!!.start()
        writeThread!!.start()
    }

    /**
     * Останавливает потоки записи/чтения ...
     */
    fun stopThreads() {
        if (readThread != null) readThread!!.interrupt()
        if (writeThread != null) writeThread!!.interrupt()
    }

    /**
     * Возвращает статус TCP-соединения.
     * Внимание! TCP-соединение может быть установлено, но Handshake может быть не пройден.
     **/
    fun isConnected() = socket!!.isConnected && handlerThread?.key != null

    /**
     * Закрывает TCP-соединение с сервером.
     **/
    fun close() = async {
        socket!!.close()
    }

    /**
     * Методы для "внешней" отправки (на уровне сети, без шифрования)
     **/
    fun sendArray(vararg params: Any) = sendString(params.toJsonArray().toString())

    /**
     * Методы для "внешней" отправки (на уровне сети, без шифрования)
     **/
    fun sendString(string: String) = async {
        try {
            writeThread!!.messagesQueue.put(string)
        } catch (e: InterruptedException) {
            // Ignore
        }
    }

    fun sendMessage(event: String, message: JsonModel? = null) = async {
        if (!isConnected()) {
            connectionStateLiveData.postValue(ConnectionState.CONNECTION_CLOSED)
        } else {
            val iv = randomBase64String(16)
            val salt = randomBase64String(32)
            val json = message?.toJSON() ?: JSONObject()
            val hmac = Base64.encodeToString(getHmac(handlerThread!!.key!!, event + json + salt), Base64.NO_WRAP)
            val payload = arrayOf(event, json, salt)
                    .toJsonArray()
                    .toString()

            // Шифруем данные ...
            val encryptedPayload = encryptAES(handlerThread!!.key!!, iv, payload)

            // Отправим массив данных.
            sendArray("msg", iv, encryptedPayload, hmac)
        }
    }

    /**
     * Добавляет кэллбэк в список.
     *
     * Метод публичный, т.к. используется в inline-методах.
     * Ни в коем случае не использовать вне протокола!
     *
     * Асинхронный, т.к. дурак может выполнить его в UI-потоке,
     * а как вы знаете, я против любых операций, кроме операций с дизайном в UI потоке...
     */
    fun <T : JsonModel> addToCallbacks(event: String?, clazz: KClass<T>, resultFunction: (T) -> (Unit), once: Boolean) = async {
        readCallbacks.plusAssign(ReadCallback(resultFunction, clazz, event, once))
    }

    /**
     * Подписывает кэллбэк на постоянное получение данных. Не будет даже после разрыва/потери соединения.
     */
    inline fun <reified T : JsonModel> listenMessage(event: String, noinline resultFunction: (T) -> (Unit)) {
        addToCallbacks(event, T::class, resultFunction, false)
    }

    /**
     * Подписывает кэллбэк на единственное получение данных.
     * Все "одноразовые" кэллбэки, которые не получили данные, будут удалены после разрыва/потери соединения.
     */
    inline fun <reified T : JsonModel> listenMessageOnce(event: String, noinline resultFunction: (T) -> (Unit)) {
        addToCallbacks(event, T::class, resultFunction, true)
    }

    /**
     * Подписывает кэллбэк на получение ошибок во всех сообщениях.
     */
    fun listenErrorCodes(resultFunction: (Int) -> (Unit)) {
        errorsMessagesCallbacks.plusAssign(resultFunction)
    }

    /**
     * Подписывает кэллбэк на единственный ответ, шифрует и отправляет сообщение на сервер.
     */
    inline fun <reified T : JsonModel> makeRequest(event: String, message: JsonModel? = null, noinline resultFunction: (T) -> (Unit)) {
        listenMessageOnce(event, resultFunction)
        sendMessage(event, message)
    }

    /**
     * Метод, передающий в callback'и указанного эвента пришедшую информацию с сервера.
     * Предварительно выполняет чтение и парсинг JSON.
     */
    internal fun notifyCallbacks(event: String, json: String) = async {
        val iterator = readCallbacks.iterator()

        /* Тут будет выгоднее использовать Iterator, т.к. при его использовании мы можем удалить
           обьект из списка в цикле, не боясь ConcurrentModificationException. */
        while (iterator.hasNext()) {
            val next = iterator.next()
            val instance = next.clazz.java.newInstance()

            // Загоним в него данные
            instance.readResponse(JSONObject(json))

            // Оперируем с кэллбэком
            if (instance.containsError()) errorsMessagesCallbacks.forEach { it(instance.error) }
            if (next.event != event) continue
            if (next.once) iterator.remove()

            // Крикнем в окно (для IDEA: не ори на отсуствие проверку типов)
            @Suppress("UNCHECKED_CAST")
            (next.resultFunction as (JsonModel) -> (Unit))(instance)
        }
    }
}