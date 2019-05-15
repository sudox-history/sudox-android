package com.sudox.android.common.helpers.livedata

import android.arch.lifecycle.LiveData
import android.os.Looper
import com.sudox.common.helper.any
import com.sudox.common.helper.mockMainThreadHandler
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*
import java.util.concurrent.Semaphore
import kotlin.random.Random

@RunWith(PowerMockRunner::class)
@PrepareForTest(ActiveSingleLiveEvent::class, Looper::class, LiveData::class)
class ActiveSingleLiveEventTest : Assert() {

    @Test
    fun testSetValue_without_filter_and_listeners() {
        val liveData = PowerMockito.spy(ActiveSingleLiveEvent<Int>())
        val testValue = Random.nextInt()

        // Testing ...
        ActiveSingleLiveEvent::class.java
                .getDeclaredField("isActive")
                .apply { isAccessible = true }
                .setBoolean(liveData, false)

        liveData.value = testValue
        liveData.value = testValue

        // Verifying, that value not posted ...
        assertNull(liveData.value)

        // Verifying, that value was added to queue
        val valuesQueue = ActiveSingleLiveEvent::class.java
                .getDeclaredField("valuesQueue")
                .apply { isAccessible = true }
                .get(liveData) as Queue<Int>

        assertArrayEquals(intArrayOf(testValue), valuesQueue.toIntArray())
    }

    @Test
    fun testSetValue_with_filter_but_without_listeners() {
        val liveData = PowerMockito.spy(ActiveSingleLiveEvent<Int>())
        val testData = intArrayOf(45, 100)

        // Bind filter ...
        liveData.filter = { old, new -> old < new!! }

        // Testing ...
        ActiveSingleLiveEvent::class.java
                .getDeclaredField("isActive")
                .apply { isAccessible = true }
                .setBoolean(liveData, false)

        liveData.value = testData[0]
        liveData.value = testData[1]

        // Verifying ...
        val valuesQueue = ActiveSingleLiveEvent::class.java
                .getDeclaredField("valuesQueue")
                .apply { isAccessible = true }
                .get(liveData) as Queue<Int>

        assertArrayEquals(intArrayOf(100), valuesQueue.toIntArray())
    }

    @Test
    fun testSetValue_without_filter_but_with_subscribers() {
        val liveData = PowerMockito.spy(ActiveSingleLiveEvent<Int>())
        val semaphore = Semaphore(1)
        val testValues = intArrayOf(1, 8, 8)
        val observedValues = ArrayList<Int>()

        mockMainThreadHandler()

        // Allow invoke setValue on main thread
        PowerMockito.mockStatic(LiveData::class.java)
        PowerMockito.doNothing().`when`(LiveData::class.java, "assertMainThread", any())

        // Testing ...
        ActiveSingleLiveEvent::class.java
                .getDeclaredField("isActive")
                .apply { isAccessible = true }
                .setBoolean(liveData, false)

        testValues.forEach { liveData.value = it }

        // Try to duplicate data
        liveData.value = testValues.last()

        // Bind listener ...
        liveData.observeForever {
            observedValues.plusAssign(it!!)

            if (observedValues.size == testValues.size) {
                semaphore.release()
            }
        }

        semaphore.acquire()

        // Try to duplicate latest data
        liveData.value = testValues.last()

        // Verifying
        val valuesQueue = ActiveSingleLiveEvent::class.java
                .getDeclaredField("valuesQueue")
                .apply { isAccessible = true }
                .get(liveData) as Queue<Int>

        assertArrayEquals(testValues, observedValues.toIntArray())
        assertTrue(valuesQueue.isEmpty())
    }

    @Test
    fun testSetValue_with_filter_and_subscribers() {
        val liveData = PowerMockito.spy(ActiveSingleLiveEvent<Int>())
        val semaphore = Semaphore(1)
        val testValues = intArrayOf(1, 8, 9)
        val validValues = intArrayOf(9, 9, 8)
        val observedValues = ArrayList<Int>()

        mockMainThreadHandler()

        // Allow invoke setValue on main thread
        PowerMockito.mockStatic(LiveData::class.java)
        PowerMockito.doNothing().`when`(LiveData::class.java, "assertMainThread", any())

        // Bind filter
        liveData.filter = { old, new -> old < new!! }

        // Testing ...
        ActiveSingleLiveEvent::class.java
                .getDeclaredField("isActive")
                .apply { isAccessible = true }
                .setBoolean(liveData, false)

        testValues.forEach { liveData.value = it }

        // Try to duplicate data
        liveData.value = testValues.last()

        // Bind listener ...
        liveData.observeForever {
            observedValues.plusAssign(it!!)

            if (observedValues.size == testValues.size) {
                semaphore.release()
            }
        }

        semaphore.acquire()

        // Try to duplicate latest data
        liveData.value = testValues.last()
        liveData.value = 8

        // Verifying
        val valuesQueue = ActiveSingleLiveEvent::class.java
                .getDeclaredField("valuesQueue")
                .apply { isAccessible = true }
                .get(liveData) as Queue<Int>

        assertArrayEquals(validValues, observedValues.toIntArray())
        assertTrue(valuesQueue.isEmpty())
    }
}