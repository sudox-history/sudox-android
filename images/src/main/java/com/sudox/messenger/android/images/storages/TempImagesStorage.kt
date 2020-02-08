package com.sudox.messenger.android.images.storages

import android.graphics.Paint
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.sudox.messenger.android.images.ImageLoadingListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.Semaphore
import kotlin.random.Random

val loadingSemaphore = Semaphore(1)
val loadingImages = HashMap<Long, ArrayList<ImageLoadingListener>>()
val loaded = HashSet<Long>()

fun loadImageById(listener: ImageLoadingListener, id: Long) = GlobalScope.launch {
    listener.onLoadingStarted()
    loadingSemaphore.acquire()

    if (loadingImages.containsKey(id)) {
        loadingImages[id]!!.add(listener)
        loadingSemaphore.release()
        return@launch
    }

    loadingImages[id] = ArrayList()
    loadingImages[id]!!.add(listener)
    loadingSemaphore.release()

    val bitmap = createBitmap(128, 128).applyCanvas {
        drawText(id.toString(), 64F, 64F, Paint())
    }

    if (!loaded.contains(id)) {
        Thread.sleep(Random.nextLong(200, 1000))
        loaded.add(id)
    }

    loadingSemaphore.acquire()

    loadingImages[id]!!.forEach {
        it.onLoadingCompleted(bitmap)
    }

    loadingImages.remove(id)
    loadingSemaphore.release()
}

fun stopImageLoading(listener: ImageLoadingListener, id: Long) = GlobalScope.launch {
    loadingSemaphore.acquire()

    if (loadingImages[id] == null) {
        loadingSemaphore.release()
        return@launch
    }

    val iterator = loadingImages[id]!!.iterator()

    while (iterator.hasNext()) {
        if (iterator.next() == listener) {
            iterator.remove()
            break
        }
    }
}