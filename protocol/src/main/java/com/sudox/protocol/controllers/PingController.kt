package com.sudox.protocol.controllers

import android.os.SystemClock
import com.sudox.protocol.ProtocolController
import java.util.LinkedList

class PingController(val protocolController: ProtocolController) {

    private var pingRequestedOrDelivered: Boolean = false

    companion object {
        internal val PING_PACKET_NAME = "png".toByteArray()
        internal const val PING_PACKET_SLICES_COUNT = 1

        internal const val PING_SEND_INTERVAL_IN_MILLIS = 2000L
        internal const val PING_CHECK_INTERVAL_IN_MILLIS = 6000L
        internal const val PING_SEND_TASK_ID = 0
        internal const val PING_CHECK_TASK_ID = 1
    }

    internal fun startPingCycle() {
        pingRequestedOrDelivered = true
    }

    internal fun handlePing() {
        if (pingRequestedOrDelivered) {
            sendPingPacket()
        } else {
            pingRequestedOrDelivered = true
        }
    }

    internal fun schedulePingSendTask() {
        val threadHandler = protocolController.threadHandler!!
        val time = SystemClock.uptimeMillis() + PING_SEND_INTERVAL_IN_MILLIS

        threadHandler.removeCallbacksAndMessages(PING_SEND_TASK_ID)
        threadHandler.removeCallbacksAndMessages(PING_CHECK_TASK_ID)
        threadHandler.postAtTime(::sendPing, PING_SEND_TASK_ID, time)
    }

    internal fun schedulePingCheckTask() {
        val threadHandler = protocolController.threadHandler!!
        val time = SystemClock.uptimeMillis() + PING_CHECK_INTERVAL_IN_MILLIS

        threadHandler.postAtTime(::checkPing, PING_CHECK_TASK_ID, time)
    }

    internal fun isPingPacket(slices: LinkedList<ByteArray>): Boolean {
        return slices.size == PING_PACKET_SLICES_COUNT && slices[0].contentEquals(PING_PACKET_NAME)
    }

    internal fun sendPing() {
        pingRequestedOrDelivered = false
        sendPingPacket()
        schedulePingCheckTask()
    }

    internal fun sendPingPacket() {
        protocolController.sendPacket(PING_PACKET_NAME)
    }

    internal fun checkPing() {
        if (!pingRequestedOrDelivered) {
            protocolController.restartConnection()
        }
    }
}