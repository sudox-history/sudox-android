package com.sudox.android.common.helpers

import kotlinx.coroutines.channels.ConflatedBroadcastChannel

private val CONFLATEDBROADCASTCHANNEL_STATE_CLAZZ by lazy {
    Class.forName("kotlinx.coroutines.channels.ConflatedBroadcastChannel${'$'}State")
}

private val CONFLATEDBROADCASTCHANNEL_STATE_VALUE_FIELD by lazy {
    CONFLATEDBROADCASTCHANNEL_STATE_CLAZZ
            .getDeclaredField("value")
            .apply { isAccessible = true }
}

private val CONFLATEDBROADCASTCHANNEL_STATE_UNKNOWN by lazy {
    ConflatedBroadcastChannel::class.java
            .getDeclaredField("UNDEFINED")
            .apply { isAccessible = true }
            .get(null)
}

private val CONFLATEDBROADCASTCHANNEL_STATE_FIELD by lazy {
    ConflatedBroadcastChannel::class.java
            .getDeclaredField("_state")
            .apply { isAccessible = true }
}

fun <T> ConflatedBroadcastChannel<T>.clear() {
    val state = (CONFLATEDBROADCASTCHANNEL_STATE_FIELD.get(this))

    // Reset state
    if (CONFLATEDBROADCASTCHANNEL_STATE_CLAZZ.isAssignableFrom(state::class.java)) {
        CONFLATEDBROADCASTCHANNEL_STATE_VALUE_FIELD.set(state, CONFLATEDBROADCASTCHANNEL_STATE_UNKNOWN)
    }
}