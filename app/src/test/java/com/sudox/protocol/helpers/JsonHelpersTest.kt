package com.sudox.protocol.helpers

import org.json.JSONArray
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class JsonHelpersTest {

    @Test
    fun testJsonArrayAsIterable() {
        val intArray = IntRange(1, 13).toMutableList().toIntArray()
        val jsonArray = JSONArray(intArray)
        val result = jsonArray.asIterable()

        // Validation
        assertEquals(intArray.contentHashCode(), result
                .toMutableList()
                .toTypedArray()
                .contentHashCode())
    }

    @Test
    fun testJsonArrayIterator() {
        val intArray = IntRange(1, 5).toMutableList().toIntArray()
        val jsonArray = JSONArray(intArray)
        val validCycleSteps = arrayOf(1, 2, 3, 4, 5)
        val cycleSteps = ArrayList<Int>()

        // Testing ...
        jsonArray
                .asIterable()
                .forEach { cycleSteps.add(it as Int) }

        // Validation ...
        assertArrayEquals(validCycleSteps, cycleSteps.toArray())
    }

    @Test
    fun testJsonArrayIteratorReversed() {
        val intArray = IntRange(1, 5).toMutableList().toIntArray()
        val jsonArray = JSONArray(intArray)
        val validCycleSteps = arrayOf(5, 4, 3, 2, 1)
        val cycleSteps = ArrayList<Int>()

        // Testing ...
        jsonArray
                .asIterable()
                .reversed()
                .forEach { cycleSteps.add(it as Int) }

        // Validation ...
        assertArrayEquals(validCycleSteps, cycleSteps.toArray())
    }
}