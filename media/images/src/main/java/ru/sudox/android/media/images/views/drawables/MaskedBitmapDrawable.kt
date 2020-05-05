package ru.sudox.android.media.images.views.drawables

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Shader
import ru.sudox.android.media.images.views.GlideImageView

class MaskedBitmapDrawable(
        private val bitmap: Bitmap,
        imageView: GlideImageView
) : BasicDrawable(imageView) {

    init {
        paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    override fun draw(canvas: Canvas) {
        paint.alpha = alpha
        super.draw(canvas)
    }

    override fun getIntrinsicWidth(): Int {
        return bitmap.width
    }

    override fun getIntrinsicHeight(): Int {
        return bitmap.height
    }
}