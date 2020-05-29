package ru.sudox.design.bottomsheet

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.design.bottomsheet.vos.BottomSheetVO

class BottomSheetLayout : ViewGroup {

    private var contentView: View? = null
    private var buttonsViews: Array<View>? = null
    private var titleTextView = AppCompatTextView(context).apply {
        gravity = Gravity.CENTER_HORIZONTAL

        this@BottomSheetLayout.addView(this)
    }

    private var marginBetweenTitleAndContent = 0
    private var marginBetweenContentAndButtons = 0
    private var marginBetweenButtons = 0

    var vo: BottomSheetVO? = null
        set(value) {
            titleTextView.text = value?.getTitle(context)

            buttonsViews?.forEach { removeView(it) }
            buttonsViews = value?.getButtonsViews(context)
            buttonsViews?.forEach {
                addView(it.apply {
                    layoutParams = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                    )
                })
            }

            if (contentView != null) {
                removeView(contentView)
                contentView = null
            }

            contentView = value?.getContentView(context)?.apply {
                addView(this)
            }

            field = value
            requestLayout()
            invalidate()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.bottomSheetLayoutStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.BottomSheetLayout, defStyleAttr, 0).use {
            marginBetweenTitleAndContent = it.getDimensionPixelSize(R.styleable.BottomSheetLayout_marginBetweenTitleAndContent, 0)
            marginBetweenContentAndButtons = it.getDimensionPixelSize(R.styleable.BottomSheetLayout_marginBetweenContentAndButtons, 0)
            marginBetweenButtons = it.getDimensionPixelSize(R.styleable.BottomSheetLayout_marginBetweenButtons, 0)

            setTextAppearance(titleTextView, it.getResourceIdOrThrow(R.styleable.BottomSheetLayout_titleTextAppearance))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(titleTextView, widthMeasureSpec, heightMeasureSpec)

        var needHeight = paddingTop + paddingBottom

        if (!titleTextView.text.isNullOrEmpty()) {
            needHeight += titleTextView.measuredHeight
        }

        if (contentView != null) {
            measureChild(contentView, widthMeasureSpec, heightMeasureSpec)
            needHeight += contentView!!.measuredHeight
        }

        if (!titleTextView.text.isNullOrEmpty() && contentView != null) {
            needHeight += marginBetweenTitleAndContent
        }

        if (buttonsViews != null) {
            needHeight += (buttonsViews!!.size - 1) * marginBetweenButtons + marginBetweenContentAndButtons + buttonsViews!!.sumBy {
                measureChild(it, widthMeasureSpec, heightMeasureSpec)
                it.measuredHeight
            }
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var topBorder = paddingTop
        var bottomBorder: Int

        if (!titleTextView.text.isNullOrEmpty()) {
            bottomBorder = topBorder + titleTextView.measuredHeight

            val leftBorder = measuredWidth / 2 - titleTextView.measuredWidth / 2
            val rightBorder = leftBorder + titleTextView.measuredWidth

            titleTextView.layout(leftBorder, topBorder, rightBorder, bottomBorder)

            topBorder = if (contentView != null) {
                bottomBorder + marginBetweenTitleAndContent
            } else {
                bottomBorder + marginBetweenContentAndButtons
            }
        } else {
            titleTextView.layout(0, 0, 0, 0)
        }

        if (contentView != null) {
            bottomBorder = topBorder + contentView!!.measuredHeight

            val leftBorder = measuredWidth / 2 - contentView!!.measuredWidth / 2
            val rightBorder = leftBorder + contentView!!.measuredWidth

            contentView!!.layout(leftBorder, topBorder, rightBorder, bottomBorder)

            if (buttonsViews != null) {
                topBorder = bottomBorder + marginBetweenContentAndButtons
            }
        } else {
            contentView!!.layout(0, 0, 0, 0)
        }

        buttonsViews?.forEach {
            bottomBorder = topBorder + it.measuredHeight

            val leftBorder = measuredWidth / 2 - it.measuredWidth / 2
            val rightBorder = leftBorder + it.measuredWidth

            it.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            topBorder = bottomBorder + marginBetweenButtons
        }
    }
}