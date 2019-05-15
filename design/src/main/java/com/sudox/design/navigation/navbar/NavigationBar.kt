package com.sudox.design.navigation.navbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.sudox.design.R

class NavigationBar : ViewGroup, View.OnClickListener {

    var leftButton = NavigationBarButton(context, NavigationBarButton.Type.NAVIGATION_BAR_BUTTON_LEFT)
    var rightButton = NavigationBarButton(context, NavigationBarButton.Type.NAVIGATION_BAR_BUTTON_RIGHT)
    var listener: NavigationBarListener? = null

    private var viewHeight: Int = 0
    private var horizontalPadding: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        with(context.obtainStyledAttributes(attrs, R.styleable.NavigationBar, defStyleAttr,
                R.style.NavigationBarStyle)) {
            horizontalPadding = getDimensionPixelSize(R.styleable.NavigationBar_horizontalPadding, 0)
            viewHeight = getDimensionPixelSize(R.styleable.NavigationBar_viewHeight, 0)
            setBackgroundColor(getColor(R.styleable.NavigationBar_backgroundColor, 0))
            recycle()
        }
    }

    init {
        initButton(leftButton)
        initButton(rightButton)
    }

    private fun initButton(navigationBarButton: NavigationBarButton) {
        navigationBarButton.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        navigationBarButton.setOnClickListener(this)
        addView(navigationBarButton)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val needWidthMeasureSpec = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val needHeightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        val buttonsMaxWidth = needWidthMeasureSpec / 2

        leftButton.maxWidth = buttonsMaxWidth
        rightButton.maxWidth = buttonsMaxWidth

        measureChild(leftButton, needWidthMeasureSpec, needHeightMeasureSpec)
        measureChild(rightButton, needWidthMeasureSpec, needHeightMeasureSpec)
        setMeasuredDimension(needWidthMeasureSpec, needHeightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val height = bottom - top
        val width = right - left

        val leftBorder = horizontalPadding
        val rightBorder = width - horizontalPadding

        leftButton.layout(leftBorder, 0, leftBorder + leftButton.measuredWidth, height)
        rightButton.layout(rightBorder - rightButton.measuredWidth, 0, rightBorder, height)
    }

    override fun onClick(view: View) {
        if (view is NavigationBarButton) {
            listener?.onButtonClick(view.type)
        }
    }

    fun resetButtonsView() {
        leftButton.resetView()
        rightButton.resetView()
    }
}