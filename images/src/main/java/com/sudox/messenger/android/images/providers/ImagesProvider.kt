package com.sudox.messenger.android.images.providers

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.LongSparseArray
import androidx.core.util.containsKey
import androidx.core.util.set
import com.sudox.messenger.android.images.R
import com.sudox.messenger.android.images.views.IMAGE_NOT_SHOWING_ID
import com.sudox.messenger.android.images.views.LoadableImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.util.Stack

/**
 * Поставщик картинок.
 *
 * Несет ответственность за:
 * 1) Кеширование картинок после их загрузки с сервера
 * 2) Контроль задач по загрузке картинки
 */
@ObsoleteCoroutinesApi
object ImagesProvider {

    private val bitmapUsingCount = LongSparseArray<Int>()
    private val imageLoadingContext = newSingleThreadContext("Sudox Images Provider")
    private val imageLoadingScope = CoroutineScope(imageLoadingContext)
    private val loadingImageViews = HashMap<Long, Stack<LoadableImageView>>()
    private val loadedImagesCache = LongSparseArray<Bitmap>()

    /**
     * Загружает изображение из хранилища и устанавливает его в заданный ImageView
     * Если изображение уже было загружено, то оно будет установлено без анимации сразу из потока,
     * с которого был вызван данный метод.
     */
    fun loadImage(imageView: LoadableImageView, id: Long) {
        val cachedBitmap = loadedImagesCache[id]
        val imageViewInstance = imageView.getInstance()

        if (cachedBitmap != null) {
            imageViewInstance.setBitmap(cachedBitmap, imageViewInstance.isShown)
            incrementCounter(id)
            return
        }

        (loadingImageViews[id] ?: Stack<LoadableImageView>().apply {
            loadingImageViews[id] = this
        }).push(imageView)

        imageLoadingScope.launch {
            (loadingImageViews[id] ?: return@launch).forEach {
                if (it.showingImageId != IMAGE_NOT_SHOWING_ID && it.showingImageId != id) {
                    unloadBitmap(it)
                }
            }

            @Suppress("BlockingMethodInNonBlockingContext")
            Thread.sleep(1000)

            // TODO: Replace to API method!
            val stack = loadingImageViews[id] ?: return@launch
            val bitmap = with((imageViewInstance.context.getDrawable(if (id == 1L) {
                R.drawable.drawable_photo_2
            } else if (id == 2L) {
                R.drawable.drawable_photo_1
            } else {
                R.drawable.drawable_photo_3
            }) as BitmapDrawable).bitmap) {
                copy(config, false)
            }

            if (!loadedImagesCache.containsKey(id)) {
                loadedImagesCache[id] = bitmap
            }

            repeat(stack.size) {
                stack.pop().getInstance().let {
                    it.setBitmap(bitmap, it.isShown)
                }

                incrementCounter(id)
            }
        }
    }

    /**
     * Убирает ImageView из очереди на загрузку.
     * Если картинка грузится из кэша, то работать не будет!
     *
     * @param imageView ImageView, в которой нужно остановить загрузку
     */
    fun stopLoading(imageView: LoadableImageView) {
        loadingImageViews[imageView.showingImageId]?.let {
            it.remove(imageView)

            if (it.size == 0) {
                loadingImageViews.remove(imageView.showingImageId)
            }
        }
    }

    /**
     * Рагружает Bitmap из заданного ImageView.
     * Если это было последнее применение данного Bitmap'а, то он также будет удален из кэша в ОЗУ.
     *
     * @param imageView ImageView, с которого нужно разгрузить Bitmap.
     */
    fun unloadBitmap(imageView: LoadableImageView) = imageView.let {
        val id = imageView.showingImageId
        val count = bitmapUsingCount[id]

        if (count != null) {
            if (bitmapUsingCount[id] == 1) {
                bitmapUsingCount.remove(id)
            } else {
                bitmapUsingCount[id]--
            }
        }

        it.getInstance().setBitmap(null, false)

        if (count == null || count - 1 == 0) {
            loadedImagesCache[id]?.recycle()
            loadedImagesCache.remove(id)
        }
    }

    private fun incrementCounter(id: Long) {
        val count = bitmapUsingCount[id]

        if (count == null) {
            bitmapUsingCount[id] = 1
        } else {
            bitmapUsingCount[id]++
        }
    }
}