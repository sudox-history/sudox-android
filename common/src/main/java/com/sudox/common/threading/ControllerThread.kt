package com.sudox.common.threading

import android.os.Handler
import android.os.HandlerThread

abstract class ControllerThread : HandlerThread {

    var threadHandler: Handler? = null

    constructor(name: String) : super(name)
    constructor(name: String, priority: Int) : super(name, priority)

    override fun onLooperPrepared() {
        threadHandler = Handler(looper)
        threadStart()
    }

    override fun interrupt() {
        threadStop()

        if (!isInterrupted) {
            super.quitSafely()
            super.interrupt()
        }
    }

    fun submitTask(runnable: () -> (Unit)) {
        if (!isInterrupted) {
            threadHandler?.post(runnable)
        }
    }

    fun submitDelayedTask(delay: Long, runnable: () -> (Unit)) {
        if (!isInterrupted) {
            threadHandler?.postDelayed(runnable, delay)
        }
    }

    fun removeAllScheduledTasks() {
        if (!isInterrupted) {
            threadHandler?.removeCallbacksAndMessages(0)
        }
    }

    abstract fun threadStart()
    abstract fun threadStop()
}