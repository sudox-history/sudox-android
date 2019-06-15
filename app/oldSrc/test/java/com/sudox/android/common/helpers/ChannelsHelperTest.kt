package com.sudox.android.common.helpers

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.junit.Assert
import org.junit.Test

class ChannelsHelperTest : Assert() {

    @Test
    fun testClear_not_initialized_channel() {
        val channel = ConflatedBroadcastChannel<Boolean>()

        // Testing ...
        channel.clear()

        // Verifying ...
        assertNull(channel.valueOrNull)
    }

    @Test
    fun testClear_initialized_channel() {
        val channel = ConflatedBroadcastChannel<Boolean>()

        // Testing ...
        channel.offer(true)
        channel.clear()

        // Verifying ...
        assertNull(channel.valueOrNull)
    }
}