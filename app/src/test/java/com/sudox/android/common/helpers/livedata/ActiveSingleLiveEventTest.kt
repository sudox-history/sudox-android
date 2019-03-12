package com.sudox.android.common.helpers.livedata

import android.arch.lifecycle.LiveData
import android.os.Looper
import com.sudox.tests.helpers.any
import com.sudox.tests.helpers.mockMainThreadHandler
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
        val testValue = Random.nextInt()
        val semaphore = Semaphore(1)
        var observedValue = 0

        mockMainThreadHandler()

        // Allow invoke setValue on main thread
        PowerMockito.mockStatic(LiveData::class.java)
        PowerMockito.doNothing().`when`(LiveData::class.java, "assertMainThread", any())

        // Testing ...
        ActiveSingleLiveEvent::class.java
                .getDeclaredField("isActive")
                .apply { isAccessible = true }
                .setBoolean(liveData, false)

        liveData.value = testValue
        liveData.observeForever {
            observedValue = it!!
            semaphore.release()
        }

        semaphore.acquire()

        // Verifying
        val valuesQueue = ActiveSingleLiveEvent::class.java
                .getDeclaredField("valuesQueue")
                .apply { isAccessible = true }
                .get(liveData) as Queue<Int>

        assertEquals(testValue, observedValue)
        assertTrue(valuesQueue.isEmpty())
    }
}