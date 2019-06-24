package com.sudox.protocol.client.network

import java.util.concurrent.Semaphore

class SocketCallbackMock : SocketCallback {

    val connectSemaphore = Semaphore(0)
    var connectedCalled: Int = 0

    var closedSemaphore = Semaphore(0)
    var closedCalled: Int = 0
    var closedErrorLast: Boolean = false

    val receiveSemaphore = Semaphore(0)
    var receivedCalled: Int = 0

    override fun socketConnected() {
        connectedCalled++
        connectSemaphore.release()
    }

    override fun socketClosed(error: Boolean) {
        closedCalled++
        closedErrorLast = error
        closedSemaphore.release()
    }

    override fun socketReceive() {
        receivedCalled++
        receiveSemaphore.release()
    }
}