package com.sudox.android.data

import kotlinx.coroutines.channels.ReceiveChannel
import java.util.*

class SubscriptionsContainer {

    private val subscriptions by lazy { Collections.synchronizedList(ArrayList<ReceiveChannel<*>?>()) }

    fun <T> addSubscription(receiveChannel: ReceiveChannel<T>): ReceiveChannel<T> = synchronized(subscriptions) {
        if (subscriptions.isEmpty() || !subscriptions.contains(receiveChannel)) {
            subscriptions.plusAssign(receiveChannel)
        }

        // For method chaining
        return receiveChannel
    }

    fun unsubscribeAll() = synchronized(subscriptions) {
        val iterator = subscriptions.iterator()

        // Prevent ConcurrentModificationException
        while (iterator.hasNext()) {
            // Fix bug with null pointer exception ...
            iterator.next()?.cancel()
            iterator.remove()
        }
    }
}