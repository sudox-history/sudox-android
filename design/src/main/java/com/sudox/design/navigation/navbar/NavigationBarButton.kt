package com.sudox.design.navigation.navbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.IntDef
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import com.sudox.design.R
import com.sudox.design.helpers.SANS_SERIF_NORMAL
import com.sudox.design.helpers.addCircleRipple

@SuppressLint("ViewConstructor")
class NavigationBarButton : AppCompatTextView {

    internal var arrowDrawable: Drawable? = null
    internal var type: Int = 0

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(Type.NAVIGATION_BAR_BUTTON_LEFT, Type.NAVIGATION_BAR_BUTTON_RIGHT)
    annotation class Type {
        companion object {
            const val NAVIGATION_BAR_BUTTON_LEFT = 0
            const val NAVIGATION_BAR_BUTTON_RIGHT = 1
        }
    }

    constructor(context: Context, @Type type: Int) : this(context, null, type)
    constructor(context: Context, attrs: AttributeSet?, @Type type: Int)
            : this(context, attrs, R.attr.navigationBarButtonsStyle, type)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, @Type type: Int)
            : super(context, attrs, defStyleAttr) {
        with(context.obtainStyledAttributes(attrs, R.styleable.NavigationBarButton, defStyleAttr,
                R.style.NavigationBarStyle_Buttons)) {
            compoundDrawablePadding = getDimensionPixelSize(R.styleable.NavigationBarButton_arrowPadding, 0)
            gravity = Gravity.CENTER_VERTICAL
            typeface = SANS_SERIF_NORMAL

            setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.styleable.NavigationBarButton_textSize, 0F))
            setTextColor(getColor(R.styleable.NavigationBarButton_textColor, 0))
            addCircleRipple()
            resetView()

            if (type == Type.NAVIGATION_BAR_BUTTON_LEFT) {
                arrowDrawable = getDrawable(R.styleable.NavigationBarButton_leftArrowDrawable)
            } else if (type == Type.NAVIGATION_BAR_BUTTON_RIGHT) {
                arrowDrawable = getDrawable(R.styleable.NavigationBarButton_rightArrowDrawable)
            }

            recycle()
        }

        this.type = type
    }

    fun setShowing(show: Boolean) {
        visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun setArrowShowing(show: Boolean) {
        if (show) {
            if (type == Type.NAVIGATION_BAR_BUTTON_LEFT) {
                setCompoundDrawables(arrowDrawable, null, null, null)
            } else if (type == Type.NAVIGATION_BAR_BUTTON_RIGHT) {
                setCompoundDrawables(null, null, arrowDrawable, null)
            }
        } else {
            setCompoundDrawables(null, null, null, null)
        }
    }

    fun resetView() {
        setShowing(false)
        setArrowShowing(false)
        isClickable = false
        text = null
    }
}