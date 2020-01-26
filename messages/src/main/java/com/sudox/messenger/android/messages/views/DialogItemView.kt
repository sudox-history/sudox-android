package com.sudox.messenger.android.messages.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.*
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.circleImageView.CircleImageView
import com.sudox.messenger.android.messages.R

class DialogItemView : ViewGroup{

    private val nameView = AppCompatTextView(context).apply { addView(this) }
    private val contentTextView = AppCompatTextView(context).apply { addView(this) }
    private val photoView = CircleImageView(context).apply { addView(this) }
    private val dateView = AppCompatTextView(context).apply { addView(this) }

    private var imageHeight = 0
    private var imageWidth = 0
    private var imageActiveColor = 0
    private var imageActiveRadius = 0
    private var imageActiveInnerRadius = 0
    private var messageStatusIcon: Drawable? = null

    private var innerImageToTextMargin = 0
    private var innerDialogNameToTopMargin= 0
    private var innerDialogNameToContentMargin= 0
    private var innerDateToTopMargin= 0
    private var innerDateToCountMargin= 0
    private var innerMessageStatusMargin = 0
    private var innerContentToRightBorderMargin = 0

    private var isNewMessage = true

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.dialogItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.DialogItemView, defStyleAttr, 0).use {
            setTextAppearance(nameView, it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogNameTextAppearance))
            setTextAppearance(dateView, it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogDateTextAppearance))
            if(!isNewMessage) {
                setTextAppearance(contentTextView, it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogContentTextAppearance))
            } else {
                setTextAppearance(contentTextView, it.getResourceIdOrThrow(R.styleable.DialogItemView_dialogNewContentTextAppearance))
            }

            imageHeight = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageHeight)
            imageWidth =  it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageWidth)

            imageActiveColor = it.getColorOrThrow(R.styleable.DialogItemView_imageActiveColor)
            imageActiveRadius = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageActiveRadius)
            imageActiveInnerRadius = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_imageActiveInnerRadius)
            messageStatusIcon = it.getDrawableOrThrow(R.styleable.DialogItemView_messageStatusIcon)

            innerImageToTextMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerImageToTextMargin)
            innerDialogNameToTopMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerDialogNameToTopMargin)
            innerDialogNameToContentMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerDialogNameToContentMargin)
            innerDateToTopMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerDateToTopMargin)
            innerDateToCountMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerDateToCountMargin)
            innerMessageStatusMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerMessageStatusMargin)
            innerContentToRightBorderMargin = it.getDimensionPixelSizeOrThrow(R.styleable.DialogItemView_innerContentToRightBorderMargin)
        }

        //dialog name view settings
        nameView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        nameView.gravity = Gravity.LEFT
        nameView.ellipsize = TextUtils.TruncateAt.END
        nameView.isSingleLine = true
        nameView.maxLines = 1
        nameView.text = "Антон"

        //dialog content view settings
        contentTextView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        contentTextView.gravity = Gravity.LEFT
        contentTextView.ellipsize = TextUtils.TruncateAt.END
        if(!isNewMessage) {
            contentTextView.maxLines = 1
        } else {
            contentTextView.maxLines = 2
        }
        contentTextView.text = "aLorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus enim ligula, tristiq"


        //dialog image settings
        photoView.layoutParams = LayoutParams(imageWidth, imageHeight)
        photoView.scaleType = ImageView.ScaleType.CENTER_CROP
        photoView.setImageDrawable(getDrawable(context,R.drawable.drawable_photo_1)) //TODO remove later

        //date view settings
        dateView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        dateView.text = "21 янв."
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChild(nameView, widthMeasureSpec, heightMeasureSpec)
        measureChild(photoView, widthMeasureSpec, heightMeasureSpec)
        measureChild(dateView, widthMeasureSpec, heightMeasureSpec)
        //measureChild(contentTextView, widthMeasureSpec, heightMeasureSpec)

        val contentTextWidth = availableWidth - imageWidth - innerImageToTextMargin - if(isNewMessage) innerContentToRightBorderMargin else innerContentToRightBorderMargin

        measureChild(contentTextView, MeasureSpec.makeMeasureSpec(contentTextWidth, MeasureSpec.EXACTLY), heightMeasureSpec)


        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        val rightBorder = width - paddingRight


        val photoLeftBorder = paddingLeft
        val photoTopBorder = paddingTop
        val photoBottomBorder = photoTopBorder + photoView.measuredHeight
        val photoRightBorder = photoLeftBorder + photoView.measuredWidth

        val dialogNameLeftBorder = photoRightBorder + innerImageToTextMargin
        val dialogNameTopBorder = paddingTop + innerDialogNameToTopMargin
        val dialogNameBottomBorder = dialogNameTopBorder + nameView.measuredHeight
        val dialogNameRightBorder = dialogNameLeftBorder + nameView.measuredWidth

        val contentLeftBorder = dialogNameLeftBorder
        val contentTopBorder = dialogNameBottomBorder + innerDialogNameToContentMargin
        val contentRightBorder = contentLeftBorder + contentTextView.measuredWidth
        val contentBottomBorder = contentTopBorder + contentTextView.measuredHeight

        dateView.layout(
                rightBorder - dateView.measuredWidth,
                paddingTop + innerDateToTopMargin,
                rightBorder,
                dateView.measuredHeight
        )

        photoView.layout(photoLeftBorder, photoTopBorder, photoRightBorder, photoBottomBorder)
        nameView.layout(dialogNameLeftBorder, dialogNameTopBorder, dialogNameRightBorder, dialogNameBottomBorder)
        contentTextView.layout(contentLeftBorder, contentTopBorder, contentRightBorder, contentBottomBorder)
    }
}