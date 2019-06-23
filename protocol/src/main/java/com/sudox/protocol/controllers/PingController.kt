package com.sudox.protocol.controllers

import android.os.SystemClock
import com.sudox.protocol.ProtocolController

internal val PING_PACKET_NAME = "png".toByteArray()
internal const val PING_SEND_TIMEOUT_IN_MILLIS = 6000L
internal const val PING_CHECK_TIMEOUT_IN_MILLIS = 1000L
internal const val PING_SEND_TASK_ID = 0
internal const val PING_CHECK_TASK_ID = 1

class PingController(val protocolController: ProtocolController) {

    private var alive: Boolean = false
    private var sendPingRunnable = ::sendPing
    private var checkPingRunnable = ::checkPing

    fun startPing() {
        alive = true
        schedulePingSendTask()
    }

    fun handlePing() {
        if (alive) {
            sendPingPacket()
        } else {
            alive = true
        }
    }

    private fun checkPing() {
        if (!alive) {
            protocolController.restartConnection()
        }
    }

    fun schedulePingSendTask() {
        val threadHandler = protocolController.handler!!
        val time = SystemClock.uptimeMillis() + PING_SEND_TIMEOUT_IN_MILLIS
        threadHandler.removeCallbacksAndMessages(PING_SEND_TASK_ID)
        threadHandler.removeCallbacksAndMessages(PING_CHECK_TASK_ID)
        threadHandler.postAtTime(sendPingRunnable, PING_SEND_TASK_ID, time)
    }

    private fun schedulePingCheckTask() {
        val threadHandler = protocolController.handler!!
        val time = SystemClock.uptimeMillis() + PING_CHECK_TIMEOUT_IN_MILLIS
        threadHandler.postAtTime(checkPingRunnable, PING_CHECK_TASK_ID, time)
    }

    private fun sendPing() {
        alive = false
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