package com.sudox.protocol.client

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProtocolClientTest : Assert() {

    @Test
    fun test() {
        val callback = object : ProtocolCallback {
            var count = 0

            override fun onMessage(message: ByteArray) {
                count++

                if (count % 5 == 0) {
                    println("Sudox: $count ${String(message)}")
                }
            }

            override fun onStarted() {
                println("Sudox Connected")
            }

            override fun onEnded() {
                println("Sudox Disconnected")
            }
        }

        val client = ProtocolClient("46.173.214.66", 5000, callback)
        client.connect()

        Thread.sleep(5000)
        val msg = Base64.decode("MgIeBAB0ZXN0HhAA0J7RgtC70LjRh9C90L4hIQ==", Base64.NO_WRAP)

        for (i in 0 until 1000) {
            client.sendMessage(msg)
            println("Sended!")
        }

        Thread.sleep(50000000)
    }
}