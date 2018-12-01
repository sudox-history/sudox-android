package com.sudox.android.data

import kotlinx.coroutines.channels.ReceiveChannel

class SubscriptionsContainer {

    private val subscriptions by lazy { ArrayList<ReceiveChannel<*>>() }

    fun <T> addSubscription(receiveChannel: ReceiveChannel<T>): ReceiveChannel<T> {
        if (!subscriptions.contains(receiveChannel)) {
            subscriptions.plusAssign(receiveChannel)
        }

        // For method chaining
        return receiveChannel
    }

    fun unsubscribeAll() {
        val iterator = subscriptions.iterator()

        // Prevent ConcurrentModificationException
        while (iterator.hasNext()) {

            // Fix bug with null pointer exception ...
            @Suppress("USELESS_ELVIS")
            iterator.next().cancel() ?: return
            iterator.remove()
        }
    }
}