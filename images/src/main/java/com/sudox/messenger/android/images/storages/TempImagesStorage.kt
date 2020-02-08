package com.sudox.messenger.android.images.storages

import android.graphics.Paint
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.sudox.messenger.android.images.ImageLoadingListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.concurrent.Semaphore

object TempImagesStorage {

    val loadingSemaphore = Semaphore(1)
    val loadingImages = HashMap<Long, ArrayList<ImageLoadingListener>>()

    fun loadImageById(listener: ImageLoadingListener, id: Long) = GlobalScope.async {
        listener.onLoadingStarted()
        loadingSemaphore.acquire()

        if (loadingImages.containsKey(id)) {
            loadingImages[id]!!.add(listener)
            loadingSemaphore.release()
            return@async
        }

        loadingSemaphore.release()

        val bitmap = createBitmap(128, 128).applyCanvas {
            drawText(id.toString(), 64F, 64F, Paint())
        }

        loadingSemaphore.acquire()

        loadingImages[id]!!.forEach {
            it.onLoadingCompleted(bitmap)
        }

        loadingSemaphore.release()
    }

    fun stopImageLoading(listener: ImageLoadingListener, id: Long) {
        loadingSemaphore.acquire()

        val iterator = loadingImages[id]?.iterator() ?: return

        while (iterator.hasNext()) {
            if (iterator.next() == listener) {
                iterator.remove()
                break
            }
        }

        loadingSemaphore.release()
    }
}