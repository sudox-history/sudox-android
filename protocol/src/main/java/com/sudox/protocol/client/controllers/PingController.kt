package com.sudox.protocol.client.controllers

import android.os.SystemClock
import com.sudox.protocol.client.ProtocolController

internal val PING_PACKET_NAME = "png".toByteArray()
internal const val PING_SEND_TIMEOUT = 6000L
internal const val PING_CHECK_TIMEOUT = 1000L
internal const val PING_SEND_TASK_ID = 0
internal const val PING_CHECK_TASK_ID = 1

class PingController(val protocolController: ProtocolController) {

    private var alive: Boolean = false
    private var sendRunnable = ::send
    private var checkRunnable = ::check

    fun start() {
        alive = true
        scheduleSendTask()
    }

    fun handlePacket() {
        if (alive) {
            sendPacket()
        } else {
            alive = true
        }
    }

    private fun check() {
        if (!alive) {
            protocolController.restartConnection()
        }
    }

    fun scheduleSendTask() {
        val threadHandler = protocolController.handler!!
        val time = SystemClock.uptimeMillis() + PING_SEND_TIMEOUT
        threadHandler.removeCallbacksAndMessages(PING_SEND_TASK_ID)
        threadHandler.removeCallbacksAndMessages(PING_CHECK_TASK_ID)
        threadHandler.postAtTime(sendRunnable, PING_SEND_TASK_ID, time)
    }

    private fun scheduleCheckTask() {
        val threadHandler = protocolController.handler!!
        val time = SystemClock.uptimeMillis() + PING_CHECK_TIMEOUT
        threadHandler.postAtTime(checkRunnable, PING_CHECK_TASK_ID, time)
    }

    private fun send() {
        alive = false
        sendPacket()
        scheduleCheckTask()
    }

    private fun sendPacket() {
        protocolController.sendPacket(arrayOf(PING_PACKET_NAME), true)
    }

    fun isPacket(name: ByteArray): Boolean {
        return name.contentEquals(PING_PACKET_NAME)
    }
}