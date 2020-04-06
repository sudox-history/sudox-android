package ru.sudox.android.media.images.factories

import android.content.Context
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import ru.sudox.android.media.images.loaders.GlideImagesModelLoader
import ru.sudox.android.media.images.entries.GlideImageRequest
import java.nio.ByteBuffer

/**
 * Обьект-фабрика загрузчика изображений.
 * Создает загрузчик изображений для Glide.
 *
 * @param context Контекст приложения/активности
 */
class GlideImagesLoaderFactory(
        val context: Context
) : ModelLoaderFactory<GlideImageRequest, ByteBuffer> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideImageRequest, ByteBuffer> {
        return GlideImagesModelLoader(context)
    }

    override fun teardown() {
    }
}