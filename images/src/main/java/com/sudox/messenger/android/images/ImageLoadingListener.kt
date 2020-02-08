package com.sudox.messenger.android.images

import android.graphics.Bitmap
import com.sudox.messenger.android.images.storages.loadImageById
import com.sudox.messenger.android.images.storages.stopImageLoading

/**
 * Слушатель загрузчика изображений.
 */
interface ImageLoadingListener {

    /**
     * Запускает загрузку картинки по ID из хранилища.
     *
     * @param id ID картинки в хранилище.
     */
    @Suppress("DeferredResultUnused")
    fun startLoading(id: Long) {
        if (getRequestedImageId() != 0L) {
            cancelLoading()
        }

        setRequestedImageId(id)
        loadImageById(this, id)
    }

    /**
     * Приостанавливает загрузку картинки
     */
    fun cancelLoading() {
        stopImageLoading(this, getRequestedImageId())
        setRequestedImageId(0)
    }

    /**
     * Возвращает ID загружаемой картинки.
     *
     * @return ID загружаемой картинки.
     */
    fun getRequestedImageId(): Long

    /**
     * Устанавливает ID загружаемой картинки.
     *
     * @param id ID загружаемой картинки.
     */
    fun setRequestedImageId(id: Long)

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