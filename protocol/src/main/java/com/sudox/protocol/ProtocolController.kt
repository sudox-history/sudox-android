package com.sudox.protocol

import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.util.Base64
import com.sudox.protocol.helpers.*
import com.sudox.protocol.models.JsonModel
import com.sudox.protocol.models.NetworkException
import com.sudox.protocol.models.dto.CoreVersionDTO
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.interfaces.ECPublicKey
import java.security.spec.InvalidKeySpecException

/**
 * Контроллер протокола (соединения).
 *
 * Выполняет функцию потока, принимает пакеты, обрабатывает их отправку.
 * Контроллирует статус соединения, выполняет соединение с сервером.
 * Может восстанавливать соединение с сервером.
 *
 * @param client - клиент протокола.
 */
class ProtocolController(private val client: ProtocolClient) : HandlerThread("SSTP Controller") {

    // Для взаимодействия с другими потоками.
    internal var handler: Handler? = null
    internal var looperPreparedCallback: (() -> (Unit))? = null
    private var requestedPing: Boolean = false
    private var isHandshakeInvoked = false
    private var getCoreVersionJob: Job? = null

    companion object {
        const val PING_SEND_TASK_TOKEN = 0
        const val PING_CHECK_TASK_TOKEN = 1
        const val SEND_PING_BEFORE_PACKET_INTERVAL = 6000L
        const val CHECK_PING_BEFORE_SEND = 2000L
    }

    override fun onLooperPrepared() {
        handler = Handler(looper)

        // Инициализация Looper'а
        if (looperPreparedCallback != null) {
            looperPreparedCallback!!()
        }
    }

    /**
     * Вызывается когда соединение будет успешно установлено.
     **/
    fun onStart() {
        // Соединение активно (нужно для функционирования ping-pong'а)
        requestedPing = true
        isHandshakeInvoked = false

        // Начинаем хэндшейк
        client.sendArray("vrf")
    }

    /**
     * Вызывается при приходе пакета.
     **/
    fun onPacket(string: String) = handler!!.post {
        try {
            val packet = JSONArray(string)
            val type = packet.optString(0) ?: return@post

            // Ищем метод-обработчик
            when (type) {
                "vrf" -> handleVerify(packet)
                "upg" -> handleUpgrade(packet)
                "msg" -> handleMessage(packet)
                "png" -> handlePing()
            }

            // Reschedule ping tasks
            handler!!.removeCallbacksAndMessages(PING_SEND_TASK_TOKEN)
            handler!!.removeCallbacksAndMessages(PING_CHECK_TASK_TOKEN)
            handler!!.postAtTime(::sendPing, PING_SEND_TASK_TOKEN, SystemClock.uptimeMillis() + SEND_PING_BEFORE_PACKET_INTERVAL)
        } catch (e: JSONException) {
            if (!isHandshakeInvoked) client.close()
        } catch (e: InvalidKeySpecException) {
            if (!isHandshakeInvoked) client.close()
        }
    }

    /**
     * Метод, вызываемый при приходе пакета ["vrf"]
     */
    private fun handleVerify(packet: JSONArray) {
        if (packet.length() >= 3) {
            val serverEcdhPublicKey = packet.optString(1)
            val serverEcdhPublicKeySignature = packet.optString(2)

            if (serverEcdhPublicKey != null
                    && serverEcdhPublicKeySignature != null
                    && serverEcdhPublicKey.matches(BASE64_REGEX)
                    && serverEcdhPublicKeySignature.matches(BASE64_REGEX)
                    && verifyData(serverEcdhPublicKey, serverEcdhPublicKeySignature)) {

                // Генерируем пару ключей (публичный и приватный)
                val keyPair = generateKeys()

                // Prepare public keySpec
                val publicKeyBytes = (keyPair.public as ECPublicKey).getPoint()
                val encodedPublicKey = Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP)

                // Generate secret keySpec
                val serverEcdhPublicKeyInstance = readPublicKey(serverEcdhPublicKey)
                val secretKey = calculateSecretKey(keyPair.private, serverEcdhPublicKeyInstance).copyOf(24)
                val secretHash = Base64.encodeToString(calculateHash(secretKey), Base64.NO_WRAP)

                // Bind keys & complete handshake
                bindEncryptionKey(secretKey)

                // Send data to the server ...
                client.sendArray("upg", encodedPublicKey, secretHash)
            } else client.close()
        } else client.close()
    }

    /**
     * Метод, вызываемый при приходе Ping'а.
     *
     * Обновляет статус прихода пакета.
     */
    private fun handlePing() {
        if (requestedPing) {
            client.sendArray("png")
        } else {
            requestedPing = true
        }
    }

    /**
     * Метод, вызываемый при приходе пакета ["upg"]
     **/
    private fun handleUpgrade(packet: JSONArray) {
        if (packet.length() >= 2) {
            val status = packet.optInt(1) == 1

            // Handshake failed!
            if (!status) {
                client.close()
                return
            }

            // Check protocol version ...
            getCoreVersionJob = compareVersions()
        }
    }

    /**
     * Сравнивает версии протоколов на сервере и в клиенте.
     *
     * Если все ОК, то отправляет в шину событий протокола - HANDSHAKE_SUCCEED
     * В противном случае отправит в шину событий протокола - OLD_PROTOCOL_VERSION
     */
    private fun compareVersions() = GlobalScope.async(Dispatchers.IO) {
        try {
            val coreVersionDTO = client
                    .makeRequest<CoreVersionDTO>("core.getVersion")
                    .await()

            // Task can be invalidated
            if (!isActive) return@async

            // Compare versions
            if (coreVersionDTO.version == ProtocolClient.VERSION) {
                isHandshakeInvoked = true
                client.connectionStateChannel.offer(ConnectionState.HANDSHAKE_SUCCEED)
            } else {
                isHandshakeInvoked = false
                client.connectionStateChannel.offer(ConnectionState.OLD_PROTOCOL_VERSION)
                client.close()
                client.kill()
            }
        } catch (e: NetworkException) {
            // Ignore ...
        }
    }

    /**
     * Отправляет пакет Ping, запускает проверку на приход ответа.
     */
    private fun sendPing() {
        requestedPing = false

        // Отправляем Ping и проверяем приход ответа через некоторые время.
        client.sendArray("png")

        // Ожидаем ответ ...
        handler!!.postAtTime(::checkPing, PING_CHECK_TASK_TOKEN, SystemClock.uptimeMillis() + CHECK_PING_BEFORE_SEND)
    }

    /**
     * Проверяет приход ответа на Ping.
     * Если ответа нет - закрывает соединение.
     */
    private fun checkPing() {
        // Закрываем соединение если нет ответа ...
        if (!requestedPing) {
            client.close()
        }
    }

    /**
     * Метод, вызываемый при приходе пакета ["msg"]
     **/
    private fun handleMessage(packet: JSONArray) {
        if (packet.length() >= 4) {
            val iv = packet.optString(1)
            val payload = packet.optString(2)
            val hmac = packet.optString(3)

            if (iv != null
                    && payload != null
                    && hmac != null
                    && iv.matches(BASE64_REGEX)
                    && payload.matches(BASE64_REGEX)
                    && hmac.matches(BASE64_REGEX)) {

                val decryptedPayloadString = decryptAES(iv, payload)
                val decryptedPayload = JSONArray(decryptedPayloadString)

                // Check length
                if (decryptedPayload.length() >= 3) {
                    val event = decryptedPayload.optString(0)
                    val message = decryptedPayload.optJSONObject(1)
                    val salt = decryptedPayload.optString(2)

                    if (event != null && message != null && salt != null && isHandshakeInvoked) {
                        val hmacRead = calculateHMAC(decryptedPayloadString)
                        val encodedHmac = Base64.encodeToString(hmacRead, Base64.NO_WRAP)

                        // Проверим HMAC'ки ...
                        if (encodedHmac == hmac) client.notifyCallbacks(event, message.toString())
                    }
                }
            }
        }
    }

    /**
     * Отправляет сообщение в формате JSON на сервер.
     *
     * @param event - название события для отправки.
     * @param message - обьект сообщения для отправки.
     */
    fun sendJsonMessage(event: String, message: JsonModel? = null) = handler!!.post {
        if (client.isValid() && isHandshakeInvoked) {
            val salt = randomBase64String(8)
            val jsonObject = message?.toJSON()
            val jsonMessage = jsonObject ?: JSONObject()
            val payload = JSONArray(arrayOf(event, jsonMessage, salt)).toString().replace("\\/", "/")

            // Encrypting
            val hmac = calculateHMAC(payload)
            val encodedHmac = Base64.encodeToString(hmac, Base64.NO_WRAP)
            val encryptedPair = encryptAES(payload)
            val encodedIv = Base64.encodeToString(encryptedPair.first, Base64.NO_WRAP)
            val encryptedPayload = String(encryptedPair.second)

            // Send packet to server
            client.sendArray("msg", encodedIv, encryptedPayload, encodedHmac)
        } else {
            client.connectionStateChannel.offer(ConnectionState.NO_CONNECTION)
        }
    }

    /**
     * Вызывается при окончании соединения.
     **/
    fun onEnd() = handler!!.post {
        handler!!.removeCallbacksAndMessages(null)

        // Cancel all internal tasks ...
        getCoreVersionJob?.cancel()
        isHandshakeInvoked = false

        // Kill connection ...
        client.kill(false)
        client.connectionStateChannel.offer(ConnectionState.CONNECTION_CLOSED)

        // Post reconnect after 1000ms
        handler!!.postDelayed({ client.connect(false) }, 1000)
    }

    override fun interrupt() {
        handler?.removeCallbacksAndMessages(null)

        // Super!
        super.interrupt()
    }
}