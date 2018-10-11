package com.sudox.protocol

import android.util.Base64
import com.sudox.protocol.helpers.encryptAES
import com.sudox.protocol.helpers.getHmac
import com.sudox.protocol.helpers.randomBase64String
import com.sudox.protocol.helpers.toJsonArray
import com.sudox.protocol.models.JsonModel
import com.sudox.protocol.models.ReadCallback
import com.sudox.protocol.models.SingleLiveEvent
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.experimental.async
import org.json.JSONObject
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class ProtocolClient @Inject constructor() {

    internal var socket: Socket? = null
    internal var controller: ProtocolController? = null
    private var reader: ProtocolReader? = null
    private var writer: ProtocolWriter? = null
    val readCallbacks = ArrayList<ReadCallback<*>>()
    val errorsMessagesCallbacks = ArrayList<(Int) -> (Unit)>()
    val connectionStateLiveData = SingleLiveEvent<ConnectionState>()

    /**
     * Метод для установки соединения с сервером.
     *
     * Если соединение уже установлено, то ничего не произойдет.
     * Если соединение не установлено, но потоки включены, то произойдет их отключение и перезапуск вместе с соединением.
     */
    fun connect(notifyAboutError: Boolean = true) {
        if (isValid()) {
            controller!!.handler.post { connectionStateLiveData.postValue(ConnectionState.HANDSHAKE_SUCCEED) }
            return
        }

        // Предотвращение случаев с открытым сокетом.
        if (notifyAboutError) kill(notifyAboutError)

        // Запускаем контроллер если он выключен.
        if (controller == null) controller = ProtocolController(this)

        // Подключение выполняем в потоке контроллера.
        if (controller!!.looperPreparedCallback == null) {
            controller!!.looperPreparedCallback = {
                controller!!.handler.post {
                    socket = Socket()
                    socket!!.keepAlive = false // У нас есть свой Ping-pong.

                    // Устанавливаем соединение
                    try {
                        socket!!.connect(InetSocketAddress("api.sudox.ru", 5000))

                        // Запускаем потоки чтения/записи.
                        kill(false)
                        startThreads()

                        // Рукопожатие.
                        controller!!.onStart()
                    } catch (e: IOException) {
                        if (notifyAboutError) connectionStateLiveData.postValue(ConnectionState.CONNECT_ERRORED)

                        // Реконнект.
                        controller!!.handler.postDelayed({ connect(false) }, 1000)
                    }
                }
            }
        } else {
            controller!!.looperPreparedCallback!!()
        }

        if (!controller!!.isAlive) controller!!.start()
    }

    private fun startThreads() {
        if (reader == null) reader = ProtocolReader(this)
        if (writer == null) writer = ProtocolWriter(this)

        // Запуск.
        if (!reader!!.isAlive || reader!!.isInterrupted) reader!!.start()
        if (!writer!!.isAlive || writer!!.isInterrupted) writer!!.start()
    }

    /**
     * Метод для проверки правильности работы сокета и прочих потоков.
     **/
    fun isValid(): Boolean {
        return socket != null
                && socket!!.isConnected
                && controller != null
                && reader != null
                && writer != null
                && controller!!.isAlive
                && reader!!.isAlive
                && writer!!.isAlive
                && !controller!!.isInterrupted
                && !reader!!.isInterrupted
                && !writer!!.isInterrupted
    }

    /**
     * Метод для отключения всех потоков, связанных с текущим сокетом.
     * Также отключает сам сокет если он открыт.
     *
     * Нужен для безопасной остановки соединения.
     **/
    fun kill(killController: Boolean = true) {
        if (reader != null && (!reader!!.isInterrupted || reader!!.isAlive)) reader!!.interrupt()
        if (controller != null && (!controller!!.isInterrupted || controller!!.isAlive) && killController) controller!!.interrupt()
        if (writer != null && (!controller!!.isInterrupted || controller!!.isAlive)) writer!!.interrupt()
        if (socket != null && (socket!!.isConnected || !socket!!.isClosed)) socket!!.close()

        // Убираем ключ.
        if (controller != null) controller!!.key = null
    }

    /**
     * Закрывает соединение (сокет).
     **/
    fun close() {
        if (socket != null && socket!!.isConnected) socket!!.close()
    }

    fun sendArray(vararg params: Any) = sendString(params.toJsonArray().toString())

    /**
     * Методы для "внешней" отправки (на уровне сети, без шифрования)
     **/
    fun sendString(string: String) {
        try {
            writer!!.messagesQueue.put(string)
        } catch (e: InterruptedException) {
            writer!!.messagesQueue = LinkedBlockingQueue()
            writer!!.messagesQueue.put(string)
        }
    }

    fun sendMessage(event: String, message: JsonModel? = null) {
        if (!isValid()) {
            connectionStateLiveData.postValue(ConnectionState.CONNECTION_CLOSED)
        } else {
            val iv = randomBase64String(16)
            val salt = randomBase64String(32)
            val json = message?.toJSON() ?: JSONObject()
            val hmac = Base64.encodeToString(getHmac(controller!!.key!!, event + json + salt), Base64.NO_WRAP)
            val payload = arrayOf(event, json, salt)
                    .toJsonArray()
                    .toString()

            // Шифруем данные ...
            val encryptedPayload = encryptAES(controller!!.key!!, iv, payload)

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
    fun <T : JsonModel> addToCallbacks(event: String?, clazz: KClass<T>, resultFunction: (T) -> (Unit), once: Boolean) {
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
    internal fun notifyCallbacks(event: String, json: String) {
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
            async { (next.resultFunction as (JsonModel) -> (Unit))(instance) }
        }
    }
}