package com.sudox.protocol.threads

import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.util.Base64
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.helpers.*
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.json.JSONArray
import org.json.JSONException
import java.security.spec.InvalidKeySpecException
import javax.crypto.interfaces.DHPublicKey

class HandlerThread(private val client: ProtocolClient) : HandlerThread("SSTPHandler") {

    lateinit var handler: Handler
    internal var key: ByteArray? = null

    // Метка "живучести" соединения (false при неполученном pong'е/ping'е)
    private var alive: Boolean = false

    override fun onLooperPrepared() {
        handler = Handler(looper)
    }

    fun handleStart() {
        // Соединение активно (нужно для функционирования ping-pong'а)
        alive = true

        // Начинаем хэндшейк
        client.sendArray("vrf") // Начинаем Handshake ...
    }

    private fun handshakeIsInvoked() = key != null

    fun handlePacket(string: String) {
        try {
            val packet = string.toJsonArray()
            val type = packet.optString(0) ?: return

            // Ищем метод-обработчик
            when (type) {
                "vrf" -> handleVerify(packet)
                "upg" -> handleUpgrade(packet)
                "msg" -> handleMessage(packet)
                "png" -> if (alive) client.sendArray("png") else alive = true
            }

            // Remove all old tasks
            handler.removeCallbacksAndMessages(0)
            handler.removeCallbacksAndMessages(1)

            // Plane tasks
            handler.postAtTime({
                alive = false

                // Send ping
                client.sendArray("png")

                // Отправляем пинг
                handler.postAtTime({ if (!alive) client.close() }, 1, SystemClock.uptimeMillis() + 2000L)
            }, 0, SystemClock.uptimeMillis() + 6000L)
        } catch (e: JSONException) {
            if (!handshakeIsInvoked()) handleEnd(true, true)
        } catch (e: InvalidKeySpecException) {
            if (!handshakeIsInvoked()) handleEnd(true, true)
        }
    }

    /**
     * Метод, вызываемый при приходе пакета ["vrf"]
     */
    private fun handleVerify(packet: JSONArray) {
        if (packet.length() >= 3) {
            val serverPublicKey = packet.optString(1)
            val serverSignature = packet.optString(2)

            if (serverPublicKey != null
                    && serverSignature != null
                    && serverPublicKey.matches(BASE64_REGEX)
                    && serverSignature.matches(BASE64_REGEX)
                    && verifyData(serverPublicKey, serverSignature)) {

                // Генерируем пару ключей (публичный и приватный)
                val keyPair = generateDhPair()

                // Prepare public key
                val publicKeyBytes = publicKeyToBytes(keyPair.public as DHPublicKey)
                val encodedPublicKey = Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP)

                // Generate secret key
                val serverPublicKeyInstance = readDhPublicKey(serverPublicKey)
                val secretKey = generateDhSecretKey(keyPair.private, serverPublicKeyInstance)
                val secretKeyHash = getHash(secretKey)
                val secretHash = Base64.encodeToString(getHash(secretKeyHash.copyOf(16)), Base64.NO_WRAP)

                // Encrypt
                key = secretKeyHash

                // Send data to the server ...
                client.sendArray("upg", encodedPublicKey, secretHash)
            } else handleEnd(true, true)
        } else handleEnd(true, true)
    }

    /**
     * Метод, вызываемый при приходе пакета ["upg"]
     **/
    private fun handleUpgrade(packet: JSONArray) {
        if (packet.length() >= 2 && packet.optInt(1) == 1) {
            client.connectionStateLiveData.postValue(ConnectionState.HANDSHAKE_SUCCEED)
        } else {
            handleEnd(true, true)
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

                val decryptedPayload = JSONArray(decryptAES(key!!, iv, payload))

                // Check length
                if (decryptedPayload.length() >= 3) {
                    val event = decryptedPayload.optString(0)
                    val message = decryptedPayload.optJSONObject(1)
                    val salt = decryptedPayload.optString(2)

                    if (event != null && message != null && salt != null && key != null) {
                        val hmacReaded = getHmac(key!!, event + message + salt)
                        val encodedHmac = Base64.encodeToString(hmacReaded, Base64.NO_WRAP)

                        // Проверим HMAC'ки ...
                        if (encodedHmac == hmac) client.notifyCallbacks(event, message.toString())
                    }
                }
            }
        }
    }

    /**
     * Метод, вызываемый при закрытии/разрыве соединения.
     */
    fun handleEnd(reasonIsAttack: Boolean = false, nextIsReconnect: Boolean = false) {
        // Удалим ключ ...
        key = null

        // Удаляем одноразовые слушатели
        client.readCallbacks.removeAll { it.once }

        // Остановим потоки
        client.stopThreads()

        // Удалим все задачи
        handler.removeCallbacksAndMessages(0)
        handler.removeCallbacksAndMessages(1)

        // Говорим слушателям, что соединение прикрыто/атаковано
        if (!reasonIsAttack && !nextIsReconnect) client.connectionStateLiveData.postValue(ConnectionState.CONNECTION_CLOSED)

        // Reconnect
        client.connect(true)
    }
}