package com.sudox.messenger.android.media.images.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.use
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.sudox.design.roundedview.RoundedImageView
import com.sudox.messenger.android.media.images.Images
import com.sudox.messenger.android.media.images.R
import com.sudox.messenger.android.media.images.entries.GlideImageRequest
import com.sudox.messenger.android.media.images.vos.ImageVO

@Suppress("unused")
const val NOT_SHOWING_IMAGE_ID = -1L

/**
 * ImageView с возможность загрузки изображения по ID с сервера
 */
open class GlideImageView : RoundedImageView {

    private var placeholderDrawable: Drawable? = null
    private var crossFadeDuration = 0

    open var vo: ImageVO? = null
        set(value) {
            Images.with(this).clear(this)

            if (value != null) {
                val imageId = value.getImageId()

                if (imageId != NOT_SHOWING_IMAGE_ID) {
                    Images.with(this)
                            .load(GlideImageRequest(imageId))
                            .placeholder(placeholderDrawable)
                            .transition(withCrossFade(crossFadeDuration))
                            .centerCrop()
                            .into(this)
                }

                // TODO: Рендер текста если нет фотографии
            }

            field = value
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.glideImageViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.GlideImageView, defStyleAttr, 0).use {
            placeholderDrawable = it.getDrawableOrThrow(R.styleable.GlideImageView_placeholderDrawable)
            crossFadeDuration = it.getIntegerOrThrow(R.styleable.GlideImageView_crossFadeDuration)
        }
    }
}