package com.sudox.design.applicationBar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat
import com.sudox.design.R
import kotlin.math.max
import kotlin.math.min

const val APPBAR_BUTTON_AT_START_TAG = "button_at_start_tag"
const val APPBAR_BUTTON_AT_END_TAG = "button_at_end_tag"

class ApplicationBar : ViewGroup {

    var buttonAtStart: ApplicationBarButton? = null
    var buttonAtEnd: ApplicationBarButton? = null

    private var titleTextId = 0
    private var titleTextView = AppCompatTextView(context)
    private var contentView: View? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.applicationBarStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ApplicationBar, defStyleAttr, 0).use {
            TextViewCompat.setTextAppearance(titleTextView, it.getResourceIdOrThrow(R.styleable.ApplicationBar_titleTextAppearance))
        }

        buttonAtStart = createButton(APPBAR_BUTTON_AT_START_TAG)
        buttonAtEnd = createButton(APPBAR_BUTTON_AT_END_TAG)
    }

    private fun createButton(tag: String): ApplicationBarButton {
        return ApplicationBarButton(context).apply {
            this.id = View.generateViewId()
            this.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            this.tag = tag

            addView(this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        measureChild(buttonAtStart, widthMeasureSpec, heightMeasureSpec)
        measureChild(buttonAtEnd, widthMeasureSpec, heightMeasureSpec)

        if (contentView != null) {
            measureChild(contentView, widthMeasureSpec, heightMeasureSpec)
        }

        val needWidth = paddingStart +
                buttonAtStart!!.measuredWidth +
                (contentView?.measuredWidth ?: 0) +
                buttonAtEnd!!.measuredWidth +
                paddingEnd

        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) {
            availableWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            min(needWidth, availableWidth)
        } else {
            needWidth
        }

        val needHeight = paddingTop + max(contentView?.measuredHeight ?: 0, buttonAtStart!!.measuredHeight) + paddingBottom
        val measuredHeight = if (heightMode == MeasureSpec.EXACTLY) {
            availableHeight
        } else if (heightMode == MeasureSpec.AT_MOST) {
            min(needHeight, availableHeight)
        } else {
            needHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = right - left
        val height = bottom - top

        val buttonAtStartLeftBorder = paddingLeft
        val buttonAtStartRightBorder = buttonAtStartLeftBorder + buttonAtStart!!.measuredWidth
        val buttonAtStartTopBorder = height / 2 - buttonAtStart!!.measuredHeight / 2
        val buttonAtStartBottomBorder = buttonAtStartTopBorder + buttonAtStart!!.measuredHeight

        buttonAtStart!!.layout(
                buttonAtStartLeftBorder,
                buttonAtStartTopBorder,
                buttonAtStartRightBorder,
                buttonAtStartBottomBorder
        )

        val buttonAtEndRightBorder = width - paddingRight
        val buttonAtEndLeftBorder = buttonAtEndRightBorder - buttonAtEnd!!.measuredWidth
        val buttonAtEndTopBorder = height / 2 - buttonAtEnd!!.measuredHeight / 2
        val buttonAtEndBottomBorder = buttonAtEndTopBorder + buttonAtEnd!!.measuredHeight

        buttonAtEnd!!.layout(
                buttonAtEndLeftBorder,
                buttonAtEndTopBorder,
                buttonAtEndRightBorder,
                buttonAtEndBottomBorder
        )

        if (contentView != null) {
            val contentViewLeftBorder = width / 2 - contentView!!.measuredWidth / 2
            val contentViewRightBorder = contentViewLeftBorder + contentView!!.measuredWidth
            val contentViewTopBorder = height / 2 - contentView!!.measuredHeight / 2
            val contentViewBottomBorder = contentViewTopBorder + contentView!!.measuredHeight

            contentView!!.layout(
                    contentViewLeftBorder,
                    contentViewTopBorder,
                    contentViewRightBorder,
                    contentViewBottomBorder
            )
        }
    }

    fun setTitle(title: String?, fromRes: Boolean = false) {
        titleTextView.text = title

        if (!fromRes) {
            titleTextId = 0
        }

        if (title != null) {
            setContentView(titleTextView)
        } else {
            setContentView(null)
        }
    }

    fun setTitle(@StringRes titleTextId: Int) {
        val title = context.getString(titleTextId)

        this.titleTextId = titleTextId
        this.setTitle(title, true)
    }

    fun setContentView(view: View?) {
        if (contentView != null && contentView != view) {
            removeViewInLayout(contentView)
        }

        contentView = view

        if (view != null) {
            addView(view)
        } else {
            requestLayout()
            invalidate()
        }
    }
}