package ru.sudox.android.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.random.Random

class ImageFetcher(
    private val context: Context,
    private val model: String
) : DataFetcher<ByteBuffer> {

    private var canceled = false

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
        val drawable = when (model) {
            "1" -> context.getDrawable(R.drawable.drawable_photo_1)
            "2" -> context.getDrawable(R.drawable.drawable_photo_2)
            "3" -> context.getDrawable(R.drawable.drawable_photo_3)
            "4" -> context.getDrawable(R.drawable.drawable_photo_4)
            "5" -> context.getDrawable(R.drawable.drawable_photo_5)
            "6" -> context.getDrawable(R.drawable.drawable_photo_6)
            "7" -> context.getDrawable(R.drawable.drawable_photo_7)
            "8" -> context.getDrawable(R.drawable.drawable_photo_8)
            "9" -> context.getDrawable(R.drawable.drawable_photo_9)
            "10" -> context.getDrawable(R.drawable.drawable_photo_10)
            "11" -> context.getDrawable(R.drawable.drawable_photo_11)
            "12" -> context.getDrawable(R.drawable.drawable_photo_12)
            else -> null
        }

        Thread.sleep(Random.nextLong(500, 2000))

        if (!canceled) {
            if (drawable != null) {
                val bitmap = (drawable as BitmapDrawable).bitmap
                var bytes: ByteArray? = null

                ByteArrayOutputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                    bytes = it.toByteArray()
                }

                callback.onDataReady(ByteBuffer.wrap(bytes!!))
            } else {
                callback.onLoadFailed(IOException())
            }
        }
    }

    override fun cleanup() {
        canceled = true
    }

    override fun cancel() {
        canceled = true
    }

    override fun getDataSource(): DataSource = DataSource.REMOTE
    override fun getDataClass(): Class<ByteBuffer> = ByteBuffer::class.java
}