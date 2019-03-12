package com.sudox.tests.helpers

import android.os.Handler
import android.os.Looper
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito
import org.mockito.stubbing.Answer
import org.powermock.api.mockito.PowerMockito
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// Main thread
private val mainThread = Executors.newSingleThreadScheduledExecutor()

fun <T> any(): T {
    Mockito.any<T>()

    // Fix Kotlin nullable issue
    return uninitialized()
}

@Throws(Exception::class)
fun mockMainThreadHandler() {
    PowerMockito.mockStatic(Looper::class.java)
    val mockMainThreadLooper = Mockito.mock(Looper::class.java)
    val mockMainThreadHandler = Mockito.mock(Handler::class.java)
    Mockito.`when`(Looper.getMainLooper()).thenReturn(mockMainThreadLooper)

    val handlerPostAnswer = Answer { invocation ->
        val runnable = invocation.arguments[0] as Runnable
        var delay = 0L

        if (invocation.arguments.size > 1) {
            delay = invocation.arguments[0] as Long
        }

        mainThread.schedule(runnable, delay, TimeUnit.MILLISECONDS)
        true
    }

    Mockito.doAnswer(handlerPostAnswer).`when`(mockMainThreadHandler).post(any())
    Mockito.doAnswer(handlerPostAnswer).`when`(mockMainThreadHandler).postDelayed(any(), anyLong())
    PowerMockito.whenNew<Handler>(Handler::class.java).withArguments(mockMainThreadLooper).thenReturn(mockMainThreadHandler)
}

private fun <T> uninitialized(): T = null as T