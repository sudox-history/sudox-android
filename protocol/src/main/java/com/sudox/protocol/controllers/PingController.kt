package com.sudox.protocol.controllers

import android.os.SystemClock
import com.sudox.protocol.ProtocolController

internal val PING_PACKET_NAME = "png".toByteArray()
internal const val SEND_TIMEOUT = 6000L
internal const val CHECK_TIMEOUT = 1000L
internal const val SEND_TASK_ID = 0
internal const val CHECK_TASK_ID = 1

class PingController(val protocolController: ProtocolController) {

    private var alive: Boolean = false
    private var sendRunnable = ::send
    private var checkRunnable = ::check

    fun start() {
        alive = true
        scheduleSendTask()
    }

    fun handle() {
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
        val time = SystemClock.uptimeMillis() + SEND_TIMEOUT
        threadHandler.removeCallbacksAndMessages(SEND_TASK_ID)
        threadHandler.removeCallbacksAndMessages(CHECK_TASK_ID)
        threadHandler.postAtTime(sendRunnable, SEND_TASK_ID, time)
    }

    private fun scheduleCheckTask() {
        val threadHandler = protocolController.handler!!
        val time = SystemClock.uptimeMillis() + CHECK_TIMEOUT
        threadHandler.postAtTime(checkRunnable, CHECK_TASK_ID, time)
    }

    private fun send() {
        alive = false
        sendPacket()
        scheduleCheckTask()
    }

    private fun sendPacket() {
        protocolController.sendPacket(PING_PACKET_NAME)
    }

    fun isPacket(name: ByteArray): Boolean {
        return name.contentEquals(PING_PACKET_NAME)
    }
}