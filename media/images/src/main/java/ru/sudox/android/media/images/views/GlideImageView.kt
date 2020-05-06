package ru.sudox.android.media.images.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.use
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.images.R
import ru.sudox.android.media.images.entries.GlideImageRequest
import ru.sudox.android.media.images.transitions.FadeTransition
import ru.sudox.android.media.images.views.drawables.GlidePlaceholderDrawable
import ru.sudox.android.media.images.views.drawables.MaskedBitmapDrawable
import ru.sudox.design.common.views.RoundedView

@Suppress("unused")
const val NOT_SHOWING_IMAGE_ID = -1L

/**
 * ImageView с возможность загрузки изображения по ID с сервера.
 * Также поддерживает установку произвольных масок.
 */
open class GlideImageView : AppCompatImageView, RoundedView {

    var maskCallback: ((Path) -> (Unit))? = null

    override var topLeftCropRadius = 0F
        set(value) {
            if (value == field) {
                return
            }

            field = value
            invalidate()
        }

    override var topRightCropRadius = 0F
        set(value) {
            if (value == field) {
                return
            }

            field = value
            invalidate()
        }

    override var bottomLeftCropRadius = 0F
        set(value) {
            if (value == field) {
                return
            }

            field = value
            invalidate()
        }

    override var bottomRightCropRadius = 0F
        set(value) {
            if (value == field) {
                return
            }

            field = value
            invalidate()
        }

    internal var placeholderColor = 0

    @Suppress("LeakingThis")
    private var placeholderDrawable = GlidePlaceholderDrawable(this)
    private var crossFadeDuration = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.glideImageViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.GlideImageView, defStyleAttr, 0).use {
            placeholderColor = it.getColorOrThrow(R.styleable.GlideImageView_placeholderColor)
            crossFadeDuration = it.getIntegerOrThrow(R.styleable.GlideImageView_crossFadeDuration)
        }
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageDrawable(if (bm != null) {
            MaskedBitmapDrawable(bm, this)
        } else {
            null
        })
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(if (drawable is BitmapDrawable) {
            MaskedBitmapDrawable(drawable.bitmap, this)
        } else {
            if (drawable is TransitionDrawable) {
                val first = drawable.getDrawable(0)
                val second = drawable.getDrawable(1)

                if (first is BitmapDrawable) {
                    drawable.setDrawable(0, MaskedBitmapDrawable(first.bitmap, this))
                }

                if (second is BitmapDrawable) {
                    drawable.setDrawable(1, MaskedBitmapDrawable(second.bitmap, this))
                }
            }

            drawable
        })
    }

    /**
     * Загружает изображение по ID
     * Дополнительно выполняет нужные преобразования.
     *
     * @param id ID изображения.
     * @param glide Менеджер запросов Glide
     */
    fun loadImage(id: Long, glide: GlideRequests) {
        cancelLoading(glide)

        glide.load(GlideImageRequest(id))
                .placeholder(placeholderDrawable)
                .transition(DrawableTransitionOptions.with(FadeTransition))
                .centerCrop()
                .into(this)
    }

    /**
     * Загружает Drawable как изображение
     * Дополнительно выполняет нужные преобразования.
     *
     * @param glide Менеджер запросов Glide
     * @param drawable Drawable для загрузки
     */
    fun loadDrawable(glide: GlideRequests, drawable: Drawable) {
        cancelLoading(glide)
        setImageDrawable(drawable)
    }

    /**
     * Приостанавливает загрузку текущего изображения
     *
     * @param glide Менеджер запросов Glide
     */
    fun cancelLoading(glide: GlideRequests) {
        glide.clear(this)
    }
}