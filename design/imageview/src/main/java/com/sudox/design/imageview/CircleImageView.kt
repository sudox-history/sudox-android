package com.sudox.design.imageview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import kotlin.math.min

/**
 * ImageView круглой формы.
 */
open class CircleImageView : ImageView {

    private val imagePaint = Paint()
    private val mergePaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT) }
    private val cropPaint = Paint().apply {
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun dispatchDraw(canvas: Canvas) {
        val imageLayer = canvas.saveLayer(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat(), imagePaint)

        super.dispatchDraw(canvas)

        val mergeLayer = canvas.saveLayer(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat(), mergePaint)
        val radius = min(measuredWidth, measuredHeight) / 2F
        val center = min(measuredWidth, measuredHeight) / 2F

        canvas.drawColor(Color.GREEN)
        canvas.drawCircle(center, center, radius, cropPaint)
        canvas.restoreToCount(mergeLayer)
        canvas.restoreToCount(imageLayer)
    }
}