package ru.sudox.api.connections.impl

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString
import ru.sudox.api.connections.Connection
import java.util.concurrent.TimeUnit

class WebSocketConnection : Connection() {

    private val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .build()

    private var webSocket: WebSocket? = null
    private val socketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            listener?.onStart()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            listener?.onClosed(t)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            listener?.onClosed(null)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            listener?.onReceive(bytes.toByteArray())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {}
    }

    override fun start(address: String, port: Int) {
        val request = Request.Builder()
                .url("ws://$address:$port")
                .build()

        webSocket = client.newWebSocket(request, socketListener)
    }

    override fun send(bytes: ByteArray) {
        webSocket?.send(bytes.toByteString())
    }

    override fun close() {
        webSocket?.close(1000, null)
        webSocket = null
    }
}