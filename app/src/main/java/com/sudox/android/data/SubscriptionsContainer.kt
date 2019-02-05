package com.sudox.android.data

import kotlinx.coroutines.channels.ReceiveChannel

class SubscriptionsContainer {

    private val subscriptions by lazy { ArrayList<ReceiveChannel<*>?>() }

    fun <T> addSubscription(receiveChannel: ReceiveChannel<T>): ReceiveChannel<T> {
        if (subscriptions.isEmpty() || !subscriptions.contains(receiveChannel)) {
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
            iterator.next()?.cancel()
            iterator.remove()
        }
    }
}