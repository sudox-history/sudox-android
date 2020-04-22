package ru.sudox.android.auth.ui.verify

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.getStringOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.android.auth.ui.R

class AuthVerifyLayout : ViewGroup {

    private var marginBetweenIconAndTitle = 0
    private var marginBetweenTitleAndDescription = 0
    private val iconImageView = AppCompatImageView(context).apply {
        this@AuthVerifyLayout.addView(this)
    }

    private val titleTextView = AppCompatTextView(context).apply {
        gravity = Gravity.CENTER_HORIZONTAL
        this@AuthVerifyLayout.addView(this)
    }

    private val descriptionTextView = AppCompatTextView(context).apply {
        gravity = Gravity.CENTER_HORIZONTAL
        this@AuthVerifyLayout.addView(this)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.authVerifyLayoutStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.AuthVerifyLayout, defStyleAttr, 0).use {
            marginBetweenIconAndTitle = it.getDimensionPixelSizeOrThrow(R.styleable.AuthVerifyLayout_marginBetweenIconAndTitle)
            marginBetweenTitleAndDescription = it.getDimensionPixelSizeOrThrow(R.styleable.AuthVerifyLayout_marginBetweenTitleAndDescription)

            iconImageView.setImageDrawable(it.getDrawableOrThrow(R.styleable.AuthVerifyLayout_iconDrawable))
            iconImageView.layoutParams = LayoutParams(
                    it.getDimensionPixelSizeOrThrow(R.styleable.AuthVerifyLayout_iconWidth),
                    it.getDimensionPixelSizeOrThrow(R.styleable.AuthVerifyLayout_iconHeight)
            )

            titleTextView.text = it.getStringOrThrow(R.styleable.AuthVerifyLayout_titleText)
            descriptionTextView.text = it.getStringOrThrow(R.styleable.AuthVerifyLayout_descriptionText)

            setTextAppearance(titleTextView, it.getResourceIdOrThrow(R.styleable.AuthVerifyLayout_titleTextAppearance))
            setTextAppearance(descriptionTextView, it.getResourceIdOrThrow(R.styleable.AuthVerifyLayout_descriptionTextAppearance))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(iconImageView, widthMeasureSpec, heightMeasureSpec)
        measureChild(titleTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(descriptionTextView, widthMeasureSpec, heightMeasureSpec)

        val needWidth = MeasureSpec.getSize(widthMeasureSpec)
        val needHeight = paddingTop +
                iconImageView.measuredHeight +
                marginBetweenIconAndTitle +
                titleTextView.measuredHeight +
                marginBetweenTitleAndDescription +
                descriptionTextView.measuredHeight +
                paddingBottom

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val iconTopBorder = paddingTop
        val iconBottomBorder = iconTopBorder + iconImageView.measuredHeight
        val iconLeftBorder = measuredWidth / 2 - iconImageView.measuredWidth / 2
        val iconRightBorder = iconLeftBorder + iconImageView.measuredWidth

        iconImageView.layout(iconLeftBorder, iconTopBorder, iconRightBorder, iconBottomBorder)

        val titleTopBorder = iconBottomBorder + marginBetweenIconAndTitle
        val titleBottomBorder = titleTopBorder + titleTextView.measuredHeight
        val titleLeftBorder = measuredWidth / 2 - titleTextView.measuredWidth / 2
        val titleRightBorder = titleLeftBorder + titleTextView.measuredWidth

        titleTextView.layout(titleLeftBorder, titleTopBorder, titleRightBorder, titleBottomBorder)

        val descriptionTopBorder = titleBottomBorder + marginBetweenTitleAndDescription
        val descriptionBottomBorder = descriptionTopBorder + descriptionTextView.measuredHeight
        val descriptionLeftBorder = measuredWidth / 2 - descriptionTextView.measuredWidth / 2
        val descriptionRightBorder = descriptionLeftBorder + descriptionTextView.measuredWidth

        descriptionTextView.layout(descriptionLeftBorder, descriptionTopBorder, descriptionRightBorder, descriptionBottomBorder)
    }
}