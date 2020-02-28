package com.sudox.messenger.android.images.providers

import android.graphics.Bitmap
import android.util.LongSparseArray
import androidx.core.util.containsKey
import androidx.core.util.set
import com.sudox.messenger.android.images.ImagesLoaderThread
import com.sudox.messenger.android.images.views.LoadableImageView
import kotlinx.coroutines.ObsoleteCoroutinesApi

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
    private val loadedImagesCache = LongSparseArray<Bitmap>()
    private val imagesLoaderThread = ImagesLoaderThread().apply { start() }

    /**
     * Загружает изображение из хранилища и устанавливает его в заданный ImageView
     * Если изображение уже было загружено, то оно будет установлено без анимации сразу из потока,
     * с которого был вызван данный метод.
     *
     * P.S.: Проигрывает анимацию изменения/установки картинки только если ImageView видна.
     */
    fun loadImage(imageView: LoadableImageView) {
        // Пробуем прогрузить картинку вне отдельного потока во избежание включения анимации после Prefetch в RecyclerView
        if (!loadFromCache(imageView, imageView.showingImageId)) {
            imagesLoaderThread.requestLoading(imageView)
        }
    }

    /**
     * Загружает изображение в ImageView, обновляет счетчик ссылок на Bitmap'ы
     *
     * @param imageView ImageView, в который нужно загрузить картинку
     * @param bitmap Bitmap который нужно загрузить в ImageView
     */
    fun loadImage(imageView: LoadableImageView, bitmap: Bitmap) {
        if (!loadedImagesCache.containsKey(imageView.showingImageId)) {
            loadedImagesCache[imageView.showingImageId] = bitmap
        }

        imageView.getInstance().let {
            it.setBitmap(bitmap, it.isShown)
        }

        incrementCounter(imageView.showingImageId)
    }

    /**
     * Пробует загрузить картинку из кэша.
     *
     * @param imageView ImageView, в который нужно загрузить картинку
     * @param id ID картинки, которую нужно загрузить в ImageView
     * @return Если картинка была загружена, то вернет true, в иных случаях всегда возвращает false.
     */
    fun loadFromCache(imageView: LoadableImageView, id: Long): Boolean {
        val cachedBitmap = loadedImagesCache[id]

        if (cachedBitmap != null) {
            imageView.getInstance().let {
                it.setBitmap(cachedBitmap, it.isShown)
            }

            incrementCounter(id)
            return true
        }

        return false
    }

    /**
     * Рагружает Bitmap из заданного ImageView.
     * Если это было последнее применение данного Bitmap'а, то он также будет удален из кэша в ОЗУ.
     *
     * @param imageView ImageView, с которого нужно разгрузить Bitmap.
     */
    fun unloadBitmap(imageView: LoadableImageView) = imageView.let {
        val id = it.showingImageId
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

    /**
     * Останавливает загрузку изображения.
     * P.S.: Не будет работать если изображение загрузилось в UI-потоке с кеша.
     *
     * @param instance ImageView, загрузку в котором нужно остановить.
     */
    fun cancelLoading(instance: LoadableImageView) {
        imagesLoaderThread.removeRequest(instance)
    }
}