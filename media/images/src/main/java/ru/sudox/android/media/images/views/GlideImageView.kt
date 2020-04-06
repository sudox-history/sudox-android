package ru.sudox.android.media.images.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.use
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import ru.sudox.design.roundedview.RoundedImageView
import ru.sudox.android.media.images.Images
import ru.sudox.android.media.images.R
import ru.sudox.android.media.images.entries.GlideImageRequest

@Suppress("unused")
const val NOT_SHOWING_IMAGE_ID = -1

/**
 * ImageView с возможность загрузки изображения по ID с сервера
 */
open class GlideImageView : RoundedImageView {

    private var placeholderDrawable: Drawable? = null
    private var crossFadeDuration = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.glideImageViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.GlideImageView, defStyleAttr, 0).use {
            placeholderDrawable = it.getDrawableOrThrow(R.styleable.GlideImageView_placeholderDrawable)
            crossFadeDuration = it.getIntegerOrThrow(R.styleable.GlideImageView_crossFadeDuration)
        }
    }

    /**
     * Загружает изображение по ID
     * Дополнительно выполняет нужные преобразования.
     *
     * @param id ID изображения.
     */
    fun loadImage(id: Long) {
        cancelLoading()

        Images.with(this)
                .load(GlideImageRequest(id))
                .placeholder(placeholderDrawable)
                .transition(withCrossFade(crossFadeDuration))
                .centerCrop()
                .into(this)
    }

    /**
     * Приостанавливает загрузку текущего изображения
     */
    fun cancelLoading() {
        Images.with(this).clear(this)
    }
}