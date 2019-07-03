package com.sudox.protocol.client

import java.util.concurrent.Semaphore

class ProtocolCallbackMock : ProtocolCallback {

    var messagesCount = 0
    var lastMessage: ByteArray? = null
    var messagesSemaphore = Semaphore(0)

    var startedCount = 0
    var startedSemaphore = Semaphore(0)

    var endedCount = 0
    var endedSemaphore = Semaphore(0)
    var connected: Boolean = false

    override fun onMessage(message: ByteArray) {
        lastMessage = message
        messagesCount++
        messagesSemaphore.release()
    }

    override fun onStarted() {
        connected = true
        startedCount++
        startedSemaphore.release()
    }

    override fun onEnded() {
        connected = false
        endedCount++
        endedSemaphore.release()
    }
}