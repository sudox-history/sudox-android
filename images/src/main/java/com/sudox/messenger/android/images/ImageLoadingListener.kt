package com.sudox.messenger.android.images

import android.graphics.Bitmap
import com.sudox.messenger.android.images.storages.loadImageById
import com.sudox.messenger.android.images.storages.stopImageLoading

const val NOT_REQUESTED_IMAGE_ID = -1L

/**
 * Слушатель загрузчика изображений.
 */
interface ImageLoadingListener {

    var requestedImageId: Long

    /**
     * Запускает загрузку картинки по ID из хранилища.
     *
     * @param id ID картинки в хранилище.
     */
    @Suppress("DeferredResultUnused")
    fun startLoading(id: Long) {
        if (requestedImageId != NOT_REQUESTED_IMAGE_ID) {
            cancelLoading()
        }

        requestedImageId = id
        loadImageById(this, id)
    }

    /**
     * Приостанавливает загрузку картинки
     */
    fun cancelLoading() {
        if (requestedImageId != NOT_REQUESTED_IMAGE_ID) {
            stopImageLoading(this, requestedImageId)
            requestedImageId = NOT_REQUESTED_IMAGE_ID
        }
    }

    /**
     * Уведомляет View о том, что картинка была загружена
     */
    fun onLoadingCompleted(bitmap: Bitmap)

    /**
     * Уведомляет View о том, что загрузка картинки началась
     */
    fun onLoadingStarted()

    /**
     * Уведомляет View о том, что изображение либо удалено,
     * либо его загрузка приостановлена
     */
    fun onLoadingStopped()
}