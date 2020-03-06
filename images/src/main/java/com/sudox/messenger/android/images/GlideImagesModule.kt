package com.sudox.messenger.android.images

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.sudox.messenger.android.images.entries.GlideImageRequest
import com.sudox.messenger.android.images.factories.GlideImagesLoaderFactory
import java.nio.ByteBuffer

/**
 * Модуль загрузки изображений на основе Glide.
 * Отвечает за регистрацию компонентов-дополнений к Glide
 *
 * P.S.: Не забудьте использовать класс Images вместо Glide.
 */
@GlideModule(glideName = "Images")
class GlideImagesModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(GlideImageRequest::class.java, ByteBuffer::class.java, GlideImagesLoaderFactory(context))
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}