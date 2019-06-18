package com.sudox.protocol.controllers

import android.os.SystemClock
import androidx.annotation.VisibleForTesting
import com.sudox.protocol.ProtocolController

internal val PING_PACKET_NAME = byteArrayOf(10, 0, 0)
internal const val PING_SEND_INTERVAL_IN_MILLIS = 6000L
internal const val PING_CHECK_INTERVAL_IN_MILLIS = 2000L
internal const val PING_SEND_TASK_ID = 0
internal const val PING_CHECK_TASK_ID = 1

class PingController(val protocolController: ProtocolController) {
    private var pingSent: Boolean = false
    private var pingReceived: Boolean = false
    private var sendPingRunnable = ::sendPing
    private var checkPingRunnable = ::checkPing

    fun startPingCycle() {
        // First ping will be send by server
        pingSent = true
        pingReceived = false
        schedulePingSendTask()
    }

    fun handlePing() {
        pingReceived = true

        if (pingSent) {
            sendPingPacket()
        }
    }

    @VisibleForTesting
    fun checkPing() {
        if (pingSent && !pingReceived) {
            protocolController.restartConnection()
        }
    }

    fun schedulePingSendTask() {
        val threadHandler = protocolController.threadHandler!!
        val time = SystemClock.uptimeMillis() + PING_SEND_INTERVAL_IN_MILLIS
        threadHandler.removeCallbacksAndMessages(PING_SEND_TASK_ID)
        threadHandler.removeCallbacksAndMessages(PING_CHECK_TASK_ID)
        threadHandler.postAtTime(sendPingRunnable, PING_SEND_TASK_ID, time)
    }

    private fun schedulePingCheckTask() {
        val threadHandler = protocolController.threadHandler!!
        val time = SystemClock.uptimeMillis() + PING_CHECK_INTERVAL_IN_MILLIS
        threadHandler.postAtTime(checkPingRunnable, PING_CHECK_TASK_ID, time)
    }

    @VisibleForTesting
    fun sendPing() {
        pingReceived = false
        pingSent = true
        sendPingPacket()
        schedulePingCheckTask()
    }

    private fun sendPingPacket() {
        protocolController.sendPacket(PING_PACKET_NAME)
    }

    fun isPingPacket(name: ByteArray): Boolean {
        return name.contentEquals(PING_PACKET_NAME)
    }
}