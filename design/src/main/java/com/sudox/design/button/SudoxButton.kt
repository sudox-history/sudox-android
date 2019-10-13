package com.sudox.design.button

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Parcelable
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatButton
import androidx.core.animation.addListener
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getIntegerOrThrow
import androidx.core.content.res.use
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.sudox.design.R

class SudoxButton : AppCompatButton {

    @VisibleForTesting
    val loadingSpinnerDrawable = CircularProgressDrawable(context)

    private var isInLoadingState = false
    private val boundsChangeAnimator = ValueAnimator().apply {
        interpolator = LinearInterpolator()

        setFloatValues(0.0F, 1.0F)
        addUpdateListener {
            requestLayout()
        }

        addListener(onEnd = {
            if (isInLoadingState) {
                loadingSpinnerDrawable.start()
                invalidate()
            } else {
                isClickable = true
            }
        }, onStart = {
            if (!isInLoadingState) {
                loadingSpinnerDrawable.stop()
                invalidate()
            } else {
                isClickable = false
            }
        })
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.buttonStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.SudoxButton, defStyleAttr, 0).use {
            val loadingSpinnerHeight = it.getDimensionPixelSizeOrThrow(R.styleable.SudoxButton_loadingSpinnerHeight)
            val loadingSpinnerWidth = it.getDimensionPixelSizeOrThrow(R.styleable.SudoxButton_loadingSpinnerWidth)

            loadingSpinnerDrawable.setBounds(0, 0, loadingSpinnerWidth, loadingSpinnerHeight)
            boundsChangeAnimator.duration = it
                    .getIntegerOrThrow(R.styleable.SudoxButton_boundsChangeAnimationDuration)
                    .toLong()
        }
    }

    init {
        clipToOutline = true
    }

    fun toggleLoadingState(toggle: Boolean) {
        isInLoadingState = toggle

        if (toggle) {
            boundsChangeAnimator.start()
        } else {
            boundsChangeAnimator.reverse()
        }
    }

    internal fun toggleLoadingStateForce(toggle: Boolean) {
        isInLoadingState = toggle

        // Button in normal state as default
        if (toggle) {
            boundsChangeAnimator.end()
        }
    }

    fun isInLoadingState(): Boolean {
        return isInLoadingState
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val currentProgress = boundsChangeAnimator.animatedValue as Float
        val currentWidth = (measuredWidth - (measuredWidth - minWidth) * currentProgress).toInt()

        setMeasuredDimension(currentWidth, measuredHeight)
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val state = parcelable as SudoxButtonState

        state.apply {
            super.onRestoreInstanceState(superState)
            readToView(this@SudoxButton)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()

        return SudoxButtonState(superState!!).apply {
            writeFromView(this@SudoxButton)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!isInLoadingState && !boundsChangeAnimator.isRunning) {
            super.onDraw(canvas)
        } else if (loadingSpinnerDrawable.isRunning) {
            val centerX = measuredWidth / 2 - loadingSpinnerDrawable.bounds.exactCenterX()
            val centerY = measuredHeight / 2 - loadingSpinnerDrawable.bounds.exactCenterY()

            canvas.translate(centerX, centerY)
            loadingSpinnerDrawable.draw(canvas)
            invalidate()
        }
    }
}