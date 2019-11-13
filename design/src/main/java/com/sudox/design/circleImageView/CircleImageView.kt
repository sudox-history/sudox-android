package com.sudox.design.circleImageView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import kotlin.math.min

class CircleImageView : androidx.appcompat.widget.AppCompatImageView {

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
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return

        val a = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), simplePaint)
        super.onDraw(canvas)

        val b = canvas.saveLayer(0f,0f, width.toFloat(), height.toFloat(), mergePaint)

        canvas.drawColor(Color.GREEN)
        canvas.drawCircle(width / 2f, height / 2f, min(width, height) / 2f, circlePaint)
        canvas.restoreToCount(b)

        canvas.restoreToCount(a)
    }
}