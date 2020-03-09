package com.sudox.design.circularupdatableview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.text.Layout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.circularupdatableview.vos.CircularUpdatableViewVO
import com.sudox.design.common.calculateViewSize
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class CircularUpdatableView : ViewGroup {

    private var viewInCenter: View? = null
    private var strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private var titleTextView = AppCompatTextView(context).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }

        gravity = Gravity.CENTER_HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        ellipsize = TextUtils.TruncateAt.END
        isSingleLine = true
        maxLines = 1

        addView(this)
    }

    var activeColor = 0
        set(value) {
            if (vo?.isActive() == true) {
                strokePaint.color = value
            }

            field = value
            invalidate()
        }

    var inactiveColor = 0
        set(value) {
            if (vo?.isActive() == false) {
                strokePaint.color = value
            }

            field = value
            invalidate()
        }

    var activeStrokeWidth = 0
        set(value) {
            if (vo?.isActive() == true) {
                strokePaint.strokeWidth = value.toFloat()
            }

            field = value
            invalidate()
        }

    var inactiveStrokeWidth = 0
        set(value) {
            if (vo?.isActive() == false) {
                strokePaint.strokeWidth = value.toFloat()
            }

            field = value
            invalidate()
        }

    var strokeRadiusDiff = 0
        set(value) {
            field = value

            requestLayout()
            invalidate()
        }

    var marginBetweenTitleAndViewInCenter = 0
        set(value) {
            field = value

            requestLayout()
            invalidate()
        }

    var vo: CircularUpdatableViewVO? = null
        set(value) {
            if (viewInCenter != null) {
                field?.unbindViewInCenter(viewInCenter!!)
            }

            if (value != null) {
                if (viewInCenter == null || !value.isViewInCenterTypeSame(viewInCenter!!)) {
                    viewInCenter = value.getViewInCenter(context).apply {
                        addViewInLayout(this, -1, LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT
                        ))
                    }
                }

                strokePaint.let {
                    if (value.isActive()) {
                        it.strokeWidth = activeStrokeWidth.toFloat()
                        it.color = activeColor
                    } else {
                        it.strokeWidth = inactiveStrokeWidth.toFloat()
                        it.color = inactiveColor
                    }
                }

                value.bindViewInCenter(viewInCenter!!)
            } else {
                if (viewInCenter != null) {
                    removeViewInLayout(viewInCenter)
                    viewInCenter = null
                }
            }

            titleTextView.text = value?.getTitle(context)
            field = value

            requestLayout()
            invalidate()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.circularUpdatableViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.CircularUpdatableView, defStyleAttr, 0).use {
            activeColor = it.getColorOrThrow(R.styleable.CircularUpdatableView_activeColor)
            inactiveColor = it.getColorOrThrow(R.styleable.CircularUpdatableView_inactiveColor)
            activeStrokeWidth = it.getDimensionPixelSizeOrThrow(R.styleable.CircularUpdatableView_activeStrokeWidth)
            inactiveStrokeWidth = it.getDimensionPixelSizeOrThrow(R.styleable.CircularUpdatableView_inactiveStrokeWidth)
            strokeRadiusDiff = it.getDimensionPixelSizeOrThrow(R.styleable.CircularUpdatableView_strokeRadiusDiff)
            marginBetweenTitleAndViewInCenter = it.getDimensionPixelSizeOrThrow(R.styleable.CircularUpdatableView_marginBetweenTitleAndViewInCenter)

            setTextAppearance(titleTextView, it.getResourceIdOrThrow(R.styleable.CircularUpdatableView_titleTextAppearance))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(viewInCenter, widthMeasureSpec, heightMeasureSpec)
        measureChild(titleTextView, MeasureSpec.makeMeasureSpec(
                viewInCenter!!.measuredWidth, MeasureSpec.EXACTLY
        ), heightMeasureSpec)

        val needWidth = paddingLeft + viewInCenter!!.measuredWidth + 2 * strokeRadiusDiff + strokePaint.strokeWidth.toInt() + paddingRight
        var needHeight = paddingTop + viewInCenter!!.measuredHeight + 2 * strokeRadiusDiff + paddingBottom

        if (titleTextView.text != null) {
            needHeight += marginBetweenTitleAndViewInCenter + titleTextView.measuredHeight
        }

        setMeasuredDimension(
                calculateViewSize(widthMeasureSpec, needWidth),
                calculateViewSize(heightMeasureSpec, needHeight)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewInCenterTopBorder = paddingTop + strokeRadiusDiff + strokePaint.strokeWidth.toInt()
        val viewInCenterBottomBorder = viewInCenterTopBorder + viewInCenter!!.measuredHeight
        val viewInCenterLeftBorder = paddingLeft + strokeRadiusDiff + strokePaint.strokeWidth.toInt()
        val viewInCenterRightBorder = viewInCenterLeftBorder + viewInCenter!!.measuredWidth

        viewInCenter!!.layout(viewInCenterLeftBorder, viewInCenterTopBorder, viewInCenterRightBorder, viewInCenterBottomBorder)

        if (titleTextView.text != null) {
            val titleTextViewTopBorder = viewInCenterBottomBorder + marginBetweenTitleAndViewInCenter
            val titleTextViewBottomBorder = titleTextViewTopBorder + titleTextView.measuredHeight

            titleTextView.layout(viewInCenterLeftBorder, titleTextViewTopBorder, viewInCenterRightBorder, titleTextViewBottomBorder)
        } else {
            titleTextView.layout(0, 0, 0, 0)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        val strokeCenterX = with(viewInCenter!!) { (left + right) / 2F }
        val strokeCenterY = with(viewInCenter!!) { (top + bottom) / 2F }
        val strokeRadius = max(viewInCenter!!.width, viewInCenter!!.height) / 2F + strokeRadiusDiff - strokePaint.strokeWidth

        canvas.drawCircle(strokeCenterX, strokeCenterY, strokeRadius, strokePaint)

        val angle = vo!!.getContentOnCircleAngle()

        if (angle in 0.0..360.0) {
            val centerX = cos(-angle) * strokeRadius + strokeCenterX
            val centerY = sin(-angle) * strokeRadius + strokeCenterY

            vo!!.drawContentOnCircle(context, canvas, centerX.toFloat(), centerY.toFloat())
        }
    }
}