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
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.circleImageView.CircleImageView
import com.sudox.messenger.android.messages.R

class DialogItemView : ViewGroup{

        private var nameView = AppCompatTextView(context).apply { addView(this) }
        private var photoView = CircleImageView(context).apply { addView(this) }

        constructor(context: Context) : this(context, null)
        constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.dialogItemViewStyle)

        @SuppressLint("Recycle")
        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
            context.obtainStyledAttributes(attrs, R.styleable.DialogItemView, defStyleAttr, 0).use {
                setTextAppearance(nameView, it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogNameTextAppearance))

                val photoHeight = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageHeight)
                val photoWidth =  it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageWidth)

                photoView.layoutParams = LayoutParams(photoWidth, photoHeight)
                photoView.scaleType = ImageView.ScaleType.CENTER_CROP
            }

            photoView.setImageDrawable(getDrawable(context,R.drawable.drawable_photo_1))

            nameView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            nameView.gravity = Gravity.CENTER_HORIZONTAL
            nameView.ellipsize = TextUtils.TruncateAt.END
            nameView.isSingleLine = true
            nameView.maxLines = 1
            nameView.text = "test"
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            measureChild(nameView, widthMeasureSpec, heightMeasureSpec)
            measureChild(photoView, widthMeasureSpec, heightMeasureSpec)

            setMeasuredDimension(widthMeasureSpec,heightMeasureSpec)
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            val photoLeftBorder = paddingLeft
            val photoTopBorder = paddingTop
            val photoBottomBorder = photoTopBorder + photoView.measuredHeight
            val photoRightBorder = photoLeftBorder + photoView.measuredWidth

            val dialogNameLeftBorder = photoRightBorder
            val dialogNameTopBorder = paddingTop
            val dialogNameBottomBorder = dialogNameTopBorder + photoView.measuredHeight
            val dialogNameRightBorder = dialogNameLeftBorder + nameView.measuredWidth

            photoView.layout(photoLeftBorder, photoTopBorder, photoRightBorder, photoBottomBorder)
            nameView.layout(dialogNameLeftBorder, dialogNameTopBorder, dialogNameRightBorder, dialogNameBottomBorder)
        }
}