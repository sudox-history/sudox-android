package ru.sudox.android.imageloader

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import java.nio.ByteBuffer

class ImageModelLoader(
    private val context: Context
) : ModelLoader<String, ByteBuffer> {

    override fun buildLoadData(model: String, width: Int, height: Int, options: Options): ModelLoader.LoadData<ByteBuffer> =
        ModelLoader.LoadData<ByteBuffer>(ObjectKey(model), ImageFetcher(context, model))

    override fun handles(model: String): Boolean = true
}