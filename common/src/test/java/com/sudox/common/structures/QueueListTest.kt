package com.sudox.common.structures

import org.junit.Assert
import org.junit.Test

class QueueListTest : Assert() {

    @Test
    fun testEmpty() {
        val queueList = QueueList<Int>()
        assertEquals(0, queueList.size())

        queueList.shift()
        assertEquals(0, queueList.size())
    }

    @Test
    fun testSingle() {
        val queueList = QueueList<Int>()
        queueList.push(1)
        assertEquals(1, queueList.size())
        assertEquals(1, queueList.shift())
        assertEquals(0, queueList.size())

        // Retry
        queueList.push(1)
        assertEquals(1, queueList.size())
        assertEquals(1, queueList.shift())
        assertEquals(0, queueList.size())
    }

    @Test
    fun testMultiple() {
        val queueList = QueueList<Int>()
        queueList.push(1)
        queueList.push(2)
        queueList.push(3)
        assertEquals(3, queueList.size())

        assertEquals(1, queueList.shift())
        assertEquals(2, queueList.size())

        assertEquals(2, queueList.shift())
        assertEquals(1, queueList.size())

        assertEquals(3, queueList.shift())
        assertEquals(0, queueList.size())

        // Retry
        queueList.push(1)
        queueList.push(2)
        queueList.push(3)
        assertEquals(3, queueList.size())

        assertEquals(1, queueList.shift())
        assertEquals(2, queueList.size())

        assertEquals(2, queueList.shift())
        assertEquals(1, queueList.size())

        assertEquals(3, queueList.shift())
        assertEquals(0, queueList.size())
    }
}