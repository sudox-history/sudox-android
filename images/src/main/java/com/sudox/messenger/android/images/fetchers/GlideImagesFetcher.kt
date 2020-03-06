package com.sudox.messenger.android.images.fetchers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.sudox.messenger.android.images.R
import com.sudox.messenger.android.images.entries.GlideImageRequest
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.random.Random

/**
 * Fetch'ер изображений для Glide.
 * Загружает изображение с сервера.
 *
 * @param context Контекст приложения/активности
 * @param model Обьект запроса на загрузку изображения.
 */
class GlideImagesFetcher(
        val context: Context,
        val model: GlideImageRequest
) : DataFetcher<ByteBuffer> {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
        val bitmap = (context.getDrawable(if (model.imageId == 1L) {
            R.drawable.drawable_photo_1
        } else if (model.imageId == 2L) {
            R.drawable.drawable_photo_2
        } else {
            R.drawable.drawable_photo_3
        }) as BitmapDrawable).bitmap

        val stream = ByteArrayOutputStream().apply { bitmap.compress(Bitmap.CompressFormat.PNG, 100, this) }
        val bytes = stream.toByteArray()
        stream.close()

        Thread.sleep(Random.nextLong(500, 1000))
        callback.onDataReady(ByteBuffer.wrap(bytes))
    }

    override fun getDataClass(): Class<ByteBuffer> {
        return ByteBuffer::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    override fun cleanup() {
    }

    override fun cancel() {
    }
}