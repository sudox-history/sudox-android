package com.sudox.protocol.implementations

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore

class BlockingQueue<T> : LinkedBlockingQueue<T>() {

    private val semaphore: Semaphore = Semaphore(0)

    override fun put(e: T) {
        super.put(e)

        // Unblock pool
        semaphore.release()
    }

    override fun poll(): T {
        semaphore.acquire()

        // Super!
        return super.poll()
    }
}