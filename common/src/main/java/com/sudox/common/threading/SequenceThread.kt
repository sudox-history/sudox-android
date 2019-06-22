package com.sudox.common.threading

import android.os.Handler
import android.os.HandlerThread

abstract class SequenceThread(name: String) : HandlerThread(name) {

    var handler: Handler? = null

    override fun onLooperPrepared() {
        handler = Handler(looper)
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
            handler?.post(runnable)
        }
    }

    fun submitDelayedTask(delay: Long, runnable: () -> (Unit)) {
        if (!isInterrupted) {
            handler?.postDelayed(runnable, delay)
        }
    }

    fun removeAllScheduledTasks() {
        if (!isInterrupted) {
            handler?.removeCallbacksAndMessages(0)
        }
    }

    abstract fun threadStart()
    abstract fun threadStop()
}