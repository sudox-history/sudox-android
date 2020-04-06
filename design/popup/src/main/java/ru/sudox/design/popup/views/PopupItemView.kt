package ru.sudox.design.popup.views

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.design.popup.R
import ru.sudox.design.popup.vos.PopupItemVO
import kotlin.math.max

class PopupItemView : ViewGroup {

    var marginBetweenIconAndTitle = 0
        set(value) {
            field = value

            if (vo != null) {
                vo = vo
            }
        }

    var inactiveTitleColor = 0
        set(value) {
            field = value

            if (vo != null) {
                vo = vo
            }
        }

    var activeTitleColor = 0
        set(value) {
            field = value

            if (vo != null) {
                vo = vo
            }
        }

    var iconHeight = 0
        set(value) {
            field = value

            if (vo != null) {
                vo = vo
            }
        }

    var iconWidth = 0
        set(value) {
            field = value

            if (vo != null) {
                vo = vo
            }
        }

    private var iconView: View? = null
        set(value) {
            if (field != null) {
                vo?.detachIconView(iconView!!)
                removeView(iconView)
            }

            field = value?.apply {
                layoutParams = LayoutParams(iconWidth, iconHeight)
                addView(this)
            }

            requestLayout()
            invalidate()
        }

    private var titleTextView = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        ellipsize = TextUtils.TruncateAt.END
        isSingleLine = true
        maxLines = 1

        addView(this)
    }

    var vo: PopupItemVO<*>? = null
        set(value) {
            titleTextView.text = value?.title
            titleTextView.setTextColor(if (value?.isActive == true) {
                activeTitleColor
            } else {
                inactiveTitleColor
            })

            if (field != null && iconView != null) {
                field!!.detachIconView(iconView!!)
            }

            iconView = value?.getIconView(context)
            value?.configureIconView(this, iconView!!)

            field = value
            requestLayout()
            invalidate()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.popupItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.PopupItemView, defStyleAttr, 0).use {
            marginBetweenIconAndTitle = it.getDimensionPixelSize(R.styleable.PopupItemView_marginBetweenIconAndText, 0)
            inactiveTitleColor = it.getColorOrThrow(R.styleable.PopupItemView_inactiveTitleColor)
            activeTitleColor = it.getColorOrThrow(R.styleable.PopupItemView_activeTitleColor)
            iconHeight = it.getDimensionPixelSizeOrThrow(R.styleable.PopupItemView_iconHeight)
            iconWidth = it.getDimensionPixelSizeOrThrow(R.styleable.PopupItemView_iconWidth)

            setTextAppearance(titleTextView, it.getResourceIdOrThrow(R.styleable.PopupItemView_titleTextAppearance))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

        titleTextView.measure(widthSpec, heightMeasureSpec)
        iconView!!.measure(widthSpec, heightMeasureSpec)

        val needHeight = paddingTop +
                max(iconView!!.measuredHeight, titleTextView.measuredHeight) +
                paddingBottom

        val needWidth = max(paddingLeft +
                iconView!!.measuredWidth +
                marginBetweenIconAndTitle +
                titleTextView.measuredWidth +
                paddingRight, minimumWidth)

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val iconLeftBorder = paddingLeft
        val iconRightBorder = iconLeftBorder + iconView!!.measuredWidth
        val iconTopBorder = measuredHeight / 2 - iconView!!.measuredHeight / 2
        val iconBottomBorder = iconTopBorder + iconView!!.measuredHeight

        iconView!!.layout(iconLeftBorder, iconTopBorder, iconRightBorder, iconBottomBorder)

        val titleLeftBorder = iconRightBorder + marginBetweenIconAndTitle
        val titleRightBorder = titleLeftBorder + titleTextView.measuredWidth
        val titleTopBorder = measuredHeight / 2 - titleTextView.measuredHeight / 2
        val titleBottomBorder = titleTopBorder + titleTextView.measuredHeight

        titleTextView.layout(titleLeftBorder, titleTopBorder, titleRightBorder, titleBottomBorder)
    }
}