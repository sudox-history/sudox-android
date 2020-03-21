package com.sudox.messenger.android.media.images.views

import android.content.Context
import android.util.AttributeSet
import kotlin.math.min

/**
 * Загружаемый ImageView с закругленными краями.
 */
open class GlideCircleImageView : GlideImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val radius = min(measuredWidth, measuredHeight) / 2F

        topLeftCropRadius = radius
        topRightCropRadius = radius
        bottomLeftCropRadius = radius
        bottomRightCropRadius = radius

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}