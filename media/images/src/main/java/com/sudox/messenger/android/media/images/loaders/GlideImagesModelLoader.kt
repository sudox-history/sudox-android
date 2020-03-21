package com.sudox.messenger.android.media.images.loaders

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.sudox.messenger.android.media.images.entries.GlideImageRequest
import com.sudox.messenger.android.media.images.fetchers.GlideImagesFetcher
import java.nio.ByteBuffer

/**
 * Загрузчик информации об картинке.
 * Подготавливает данные об картинки для кэша и Fetch'ера.
 *
 * @param context Контекст приложения/активности
 */
class GlideImagesModelLoader(
        val context: Context
) : ModelLoader<GlideImageRequest, ByteBuffer> {

    override fun buildLoadData(model: GlideImageRequest, width: Int, height: Int, options: Options): ModelLoader.LoadData<ByteBuffer> {
        return ModelLoader.LoadData(ObjectKey(model), GlideImagesFetcher(context, model))
    }

    override fun handles(model: GlideImageRequest): Boolean {
        return true
    }
}