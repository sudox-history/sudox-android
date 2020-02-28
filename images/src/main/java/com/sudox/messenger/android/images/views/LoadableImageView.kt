package com.sudox.messenger.android.images.views

import com.sudox.design.imageview.ImageView
import com.sudox.messenger.android.images.providers.ImagesProvider
import kotlinx.coroutines.ObsoleteCoroutinesApi

const val IMAGE_NOT_SHOWING_ID = -1L

/**
 * Абстракция ImageView с динамически-загружаемой картинкой.
 */
@ObsoleteCoroutinesApi
interface LoadableImageView {

    var showingImageId: Long

    /**
     * Загружает изображение с хранилища.
     *
     * @param id ID изображения, которое нужно загрузить с хранилища. Если равен IMAGE_NOT_SHOWING_ID,
     * то будет установлено изображение по-умолчанию.
     */
    fun loadImage(id: Long) {
        ImagesProvider.cancelLoading(this)
        showingImageId = id

        if (id != IMAGE_NOT_SHOWING_ID) {
            ImagesProvider.loadImage(this)
        } else {
            getInstance().setBitmap(null, false)
        }
    }

    fun getInstance(): ImageView
}