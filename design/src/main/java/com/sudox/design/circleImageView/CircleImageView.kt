package com.sudox.design.circleImageView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

class CircleImageView : AppCompatImageView {

    private val simplePaint = Paint()
    private val circlePaint = Paint().apply {
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    private val mergePaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        scaleType = ScaleType.CENTER_CROP
    }

    override fun onDraw(canvas: Canvas) {
        val imageLayer = canvas.saveLayer(0F, 0F, width.toFloat(), height.toFloat(), simplePaint)

        super.onDraw(canvas)

        val mergeLayer = canvas.saveLayer(0F, 0F, width.toFloat(), height.toFloat(), mergePaint)

        canvas.drawColor(Color.GREEN)
        canvas.drawCircle(width / 2F, height / 2F, min(width, height) / 2F, circlePaint)
        canvas.restoreToCount(mergeLayer)
        canvas.restoreToCount(imageLayer)
    }
}