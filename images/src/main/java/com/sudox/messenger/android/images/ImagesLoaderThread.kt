package com.sudox.messenger.android.images

import android.graphics.drawable.BitmapDrawable
import android.util.LongSparseArray
import com.sudox.messenger.android.images.providers.ImagesProvider
import com.sudox.messenger.android.images.views.IMAGE_NOT_SHOWING_ID
import com.sudox.messenger.android.images.views.LoadableImageView
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.util.ArrayDeque
import java.util.concurrent.Semaphore

@ObsoleteCoroutinesApi
class ImagesLoaderThread : Thread("Sudox Image Provider") {

    private val loadingImageViews = LongSparseArray<ArrayDeque<LoadableImageView>>()
    private val wakeUpSemaphore = Semaphore(0)

    override fun run() {
        while (!isInterrupted) {
            if (loadingImageViews.size() == 0) {
                wakeUpSemaphore.acquire()

                // Если задачи не добавились, но семафор разблокировался, то значит поток хотят закрыть
                if (loadingImageViews.size() == 0) {
                    return
                }
            }

            val imageViewQueue = loadingImageViews.valueAt(0)

            if (imageViewQueue.size == 0) {
                continue
            }

            val lastImageView = imageViewQueue.remove()
            val imageId = lastImageView.showingImageId

            if (ImagesProvider.loadFromCache(lastImageView, imageId)) {
                val iterator = imageViewQueue.iterator()

                while (iterator.hasNext()) {
                    ImagesProvider.loadFromCache(iterator.next(), imageId)
                    iterator.remove()
                }
            } else {
                imageViewQueue.forEach {
                    if (it.showingImageId != IMAGE_NOT_SHOWING_ID && it.showingImageId != imageId) {
                        ImagesProvider.unloadBitmap(it)
                    }
                }

                @Suppress("BlockingMethodInNonBlockingContext")
                sleep(1000)

                val bitmap = (lastImageView.getInstance().context.getDrawable(if (imageId == 1L) {
                    R.drawable.drawable_photo_2
                } else if (imageId == 2L) {
                    R.drawable.drawable_photo_1
                } else {
                    R.drawable.drawable_photo_3
                }) as BitmapDrawable).bitmap

                ImagesProvider.loadImage(lastImageView, bitmap)

                val iterator = imageViewQueue.iterator()

                while (iterator.hasNext()) {
                    ImagesProvider.loadImage(iterator.next(), bitmap)
                    iterator.remove()
                }
            }

            loadingImageViews.removeAt(0)
        }
    }

    fun requestLoading(imageView: LoadableImageView) {
        val imageId = imageView.showingImageId
        var imageViewsQueue = loadingImageViews[imageId]

        if (imageViewsQueue == null) {
            imageViewsQueue = ArrayDeque()
            loadingImageViews.put(imageId, imageViewsQueue)
        }

        imageViewsQueue.add(imageView)
        wakeUpSemaphore.release()
    }

    fun removeRequest(imageView: LoadableImageView) {
        val imageId = imageView.showingImageId
        val imageViewsQueue = loadingImageViews[imageId] ?: return
        val iterator = imageViewsQueue.iterator()

        while (iterator.hasNext()) {
            if (iterator.next() == imageView) {
                iterator.remove()
                break
            }
        }

        if (imageViewsQueue.size == 0) {
            loadingImageViews.remove(imageId)
        }
    }
}