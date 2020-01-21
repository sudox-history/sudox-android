package com.sudox.messenger.android.messages.views

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
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.use
import com.sudox.design.circleImageView.CircleImageView
import com.sudox.messenger.android.messages.R

class DialogItemView : ViewGroup{
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
        private var isViewed = false

        constructor(context: Context) : this(context, null)
        constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.dialogItemView)

        @SuppressLint("Recycle")
        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
            context.obtainStyledAttributes(attrs, R.styleable.DialogItemView, defStyleAttr, 0).use {

            }

            nameView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            nameView.gravity = Gravity.CENTER_HORIZONTAL
            nameView.ellipsize = TextUtils.TruncateAt.END
            nameView.isSingleLine = true
            nameView.maxLines = 1
            nameView.text = "test"
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            measureChild(nameView, widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(widthMeasureSpec,heightMeasureSpec)
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            val nameLeftBorder = paddingLeft
            val nameTopBorder = paddingTop
            val nameBottomBorder = nameTopBorder + nameView.measuredHeight
            val nameRightBorder = nameLeftBorder + nameView.measuredWidth

            nameView.layout(nameLeftBorder, nameTopBorder, nameRightBorder, nameBottomBorder)
        }

        override fun dispatchDraw(canvas: Canvas) {
            super.dispatchDraw(canvas)
        }

        fun setCreatedByMe(createdByMe: Boolean) {

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

        fun setUserPhoto(bitmap: Bitmap?) {
            photoView.setImageBitmap(bitmap)
        }

        fun setUserPhoto(drawable: Drawable?) {
            photoView.setImageDrawable(drawable)
        }

        fun setUserName(name: String?) {
            nameView.text = name
        }
}