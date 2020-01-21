package com.sudox.messenger.android.moments.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.getStringOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.circleImageView.CircleImageView
import com.sudox.messenger.android.moments.R
import kotlin.math.min

class MomentItemView : ViewGroup {

    private var myMomentNameText: String? = null
    private var nameTextMarginRelativeToPhoto = 0

    private var viewIndicatorRadius = 0
    private var viewIndicatorActiveColor = 0
    private var viewIndicatorDefaultColor = 0
    private var viewIndicatorDefaultStrokeWidth = 0
    private var viewIndicatorActiveStrokeWidth = 0
    private var viewIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private var addIndicatorCropPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var addIndicatorStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private var addIndicatorIcon: Drawable? = null
    private var addIndicatorCropRadius = 0
    private var addIndicatorRadius = 0

    private var nameView = AppCompatTextView(context).apply { addView(this) }
    private var photoView = CircleImageView(context).apply { addView(this) }
    private var isCreatedByMe = false
    private var isViewed = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.momentItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.MomentItemView, defStyleAttr, 0).use {
            nameTextMarginRelativeToPhoto = it.getDimensionPixelSize(R.styleable.MomentItemView_nameTextMarginRelativeToPhoto, 0)
            myMomentNameText = it.getStringOrThrow(R.styleable.MomentItemView_myMomentNameText)

            setTextAppearance(nameView, it.getResourceIdOrThrow(R.styleable.MomentItemView_nameTextAppearance))

            viewIndicatorDefaultStrokeWidth =
                    it.getDimensionPixelSizeOrThrow(R.styleable.MomentItemView_viewIndicatorDefaultStrokeWidth)
            viewIndicatorActiveStrokeWidth =
                    it.getDimensionPixelSizeOrThrow(R.styleable.MomentItemView_viewIndicatorActiveStrokeWidth)

            viewIndicatorDefaultColor = it.getColorOrThrow(R.styleable.MomentItemView_viewIndicatorDefaultColor)
            viewIndicatorActiveColor = it.getColorOrThrow(R.styleable.MomentItemView_viewIndicatorActiveColor)
            viewIndicatorRadius = it.getDimensionPixelSizeOrThrow(R.styleable.MomentItemView_viewIndicatorRadius)

            addIndicatorIcon = it.getDrawableOrThrow(R.styleable.MomentItemView_addIndicatorIcon).mutate()
            addIndicatorIcon!!.setTint(it.getColorOrThrow(R.styleable.MomentItemView_addIndicatorIconTint))
            addIndicatorIcon!!.setBounds(0, 0,
                    it.getDimensionPixelSizeOrThrow(R.styleable.MomentItemView_addIndicatorIconWidth),
                    it.getDimensionPixelSizeOrThrow(R.styleable.MomentItemView_addIndicatorIconHeight)
            )

            addIndicatorCropRadius = it.getDimensionPixelSizeOrThrow(R.styleable.MomentItemView_addIndicatorCropRadius)
            addIndicatorRadius = it.getDimensionPixelSizeOrThrow(R.styleable.MomentItemView_addIndicatorRadius)
            addIndicatorCropPaint.color = getColor(context, R.color.background_color)
            addIndicatorStrokePaint.color = it.getColorOrThrow(R.styleable.MomentItemView_addIndicatorStrokeColor)
            addIndicatorStrokePaint.strokeWidth = it.getDimensionPixelSizeOrThrow(R.styleable.MomentItemView_addIndicatorStrokeWidth)
                    .toFloat()

            val photoHeight = it.getDimensionPixelSizeOrThrow(R.styleable.MomentItemView_photoHeight)
            val photoWidth = it.getDimensionPixelSizeOrThrow(R.styleable.MomentItemView_photoWidth)

            photoView.layoutParams = LayoutParams(photoWidth, photoHeight)
            photoView.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        nameView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        nameView.gravity = Gravity.CENTER_HORIZONTAL
        nameView.ellipsize = TextUtils.TruncateAt.END
        nameView.isSingleLine = true
        nameView.maxLines = 1
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChild(nameView, widthMeasureSpec, heightMeasureSpec)
        measureChild(photoView, widthMeasureSpec, heightMeasureSpec)

        val needWidth = paddingLeft + viewIndicatorRadius * 2 + paddingRight
        val needHeight = paddingTop + viewIndicatorRadius * 2 + paddingBottom +
                (nameTextMarginRelativeToPhoto - (viewIndicatorRadius - photoView.measuredHeight / 2)) +
                nameView.measuredHeight

        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        measureChild(nameView, MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY), heightMeasureSpec)

        val measuredHeight = if (heightMode == MeasureSpec.EXACTLY) {
            availableHeight
        } else if (heightMode == MeasureSpec.AT_MOST) {
            min(needHeight, availableHeight)
        } else {
            needHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val indicatorRadiusDiffY = viewIndicatorRadius - photoView.measuredHeight / 2
        val indicatorRadiusDiffX = viewIndicatorRadius - photoView.measuredWidth / 2

        val photoTopBorder = paddingTop + indicatorRadiusDiffY
        val photoBottomBorder = photoTopBorder + photoView.measuredHeight
        val photoLeftBorder = paddingLeft + indicatorRadiusDiffX
        val photoRightBorder = photoLeftBorder + photoView.measuredWidth

        photoView.layout(photoLeftBorder, photoTopBorder, photoRightBorder, photoBottomBorder)

        val nameLeftBorder = paddingLeft
        val nameTopBorder = photoBottomBorder + nameTextMarginRelativeToPhoto
        val nameBottomBorder = nameTopBorder + nameView.measuredHeight
        val nameRightBorder = nameLeftBorder + nameView.measuredWidth

        nameView.layout(nameLeftBorder, nameTopBorder, nameRightBorder, nameBottomBorder)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        val currentRadius = viewIndicatorRadius - viewIndicatorPaint.strokeWidth
        val photoCenterX = paddingLeft + viewIndicatorRadius.toFloat()
        val photoCenterY = paddingTop + viewIndicatorRadius.toFloat()

        canvas.drawCircle(photoCenterX, photoCenterY, currentRadius, viewIndicatorPaint)

        if (isCreatedByMe) {
            val rightBorder = paddingLeft + viewIndicatorRadius * 2F
            val bottomBorder = paddingTop + viewIndicatorRadius * 2F
            val cropCenterX = rightBorder - addIndicatorCropRadius
            val cropCenterY = bottomBorder - addIndicatorCropRadius
            val strokeRadius = addIndicatorRadius - addIndicatorStrokePaint.strokeWidth

            canvas.drawCircle(cropCenterX, cropCenterY, addIndicatorCropRadius.toFloat(), addIndicatorCropPaint)
            canvas.drawCircle(cropCenterX, cropCenterY, strokeRadius, addIndicatorStrokePaint)

            val iconX = cropCenterX - addIndicatorIcon!!.bounds.exactCenterX()
            val iconY = cropCenterY - addIndicatorIcon!!.bounds.exactCenterY()

            canvas.translate(iconX, iconY)
            addIndicatorIcon!!.draw(canvas)
        }
    }

    fun setCreatedByMe(createdByMe: Boolean) {
        this.isCreatedByMe = createdByMe

        if (createdByMe) {
            setUserName(myMomentNameText!!)
        }

        requestLayout()
        invalidate()
    }

    fun setViewed(viewed: Boolean) {
        this.isViewed = viewed

        if (!viewed) {
            viewIndicatorPaint.strokeWidth = viewIndicatorActiveStrokeWidth.toFloat()
            viewIndicatorPaint.color = viewIndicatorActiveColor
        } else {
            viewIndicatorPaint.strokeWidth = viewIndicatorDefaultStrokeWidth.toFloat()
            viewIndicatorPaint.color = viewIndicatorDefaultColor
        }

        requestLayout()
        invalidate()
    }

    fun setPublisherPhoto(bitmap: Bitmap?) {
        photoView.setImageBitmap(bitmap)
    }

    fun setPublisherPhoto(drawable: Drawable?) {
        photoView.setImageDrawable(drawable)
    }

    fun setUserName(name: String?) {
        nameView.text = name
    }
}