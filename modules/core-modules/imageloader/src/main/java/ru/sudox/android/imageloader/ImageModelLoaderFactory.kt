package ru.sudox.android.imageloader

import android.content.Context
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import java.nio.ByteBuffer

/**
 * Фабрика загрузчика изображений.
 *
 * @param context Контекст приложения/активности.
 */
class ImageModelLoaderFactory(
    private val context: Context
) : ModelLoaderFactory<String, ByteBuffer> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, ByteBuffer> = ImageModelLoader(context)
    override fun teardown() {}
}