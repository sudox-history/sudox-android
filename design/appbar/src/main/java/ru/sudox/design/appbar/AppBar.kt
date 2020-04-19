package ru.sudox.design.appbar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Layout
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.design.appbar.vos.others.AppBarButtonParam
import ru.sudox.design.appbar.vos.others.NOT_USED_PARAMETER
import ru.sudox.design.common.lazyLayout
import java.util.LinkedList
import kotlin.math.abs

class AppBar : ViewGroup, View.OnClickListener {

    var vo: AppBarVO? = null
        set(value) {
            updateButtons(field?.getButtonsAtLeft(), value?.getButtonsAtLeft(), buttonsAtLeft)
            updateButtons(field?.getButtonsAtRight(), value?.getButtonsAtRight(), buttonsAtRight)

            val titleId = value?.getTitle() ?: NOT_USED_PARAMETER

            if (titleId != NOT_USED_PARAMETER) {
                titleTextView.setText(titleId)
            } else {
                titleTextView.text = null
            }

            viewAtLeft = value?.getViewAtLeft(context)?.apply {
                this@AppBar.addView(this)
            }

            viewAtRight = value?.getViewAtRight(context)?.apply {
                this@AppBar.addView(this)
            }

            field = value
        }

    private fun updateButtons(
            oldButtonsParams: Array<AppBarButtonParam>?,
            newButtonsParams: Array<AppBarButtonParam>?,
            buttonsList: LinkedList<AppCompatTextView>
    ) {
        val countDiff = (oldButtonsParams?.size ?: 0) - (newButtonsParams?.size ?: 0)

        if (countDiff >= 0) {
            repeat(countDiff) {
                removeView(buttonsList.removeLast())
            }
        } else if (countDiff < 0) {
            repeat(abs(countDiff)) {
                buttonsList.addFirst(createButton())
            }
        }

        buttonsList.forEachIndexed { index, it ->
            configureButton(it, newButtonsParams!![index])
        }
    }

    var buttonsStyleId: Int = 0
        set(value) {
            buttonsAtLeft.forEach { setTextAppearance(it, value) }
            buttonsAtRight.forEach { setTextAppearance(it, value) }

            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenViewsAndButtons: Int = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var callback: ((Int) -> (Unit))? = null

    private var viewAtLeft: View? = null
    private var viewAtRight: View? = null
    private var buttonsAtLeft = LinkedList<AppCompatTextView>()
    private var buttonsAtRight = LinkedList<AppCompatTextView>()
    private var titleTextView = createTextView()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.appBarStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.AppBar, defStyleAttr, 0).use {
            buttonsStyleId = it.getResourceIdOrThrow(R.styleable.AppBar_buttonsStyle)
            marginBetweenViewsAndButtons = it.getDimensionPixelSize(R.styleable.AppBar_marginBetweenViewsAndButtons, 0)

            setTextAppearance(titleTextView, it.getResourceIdOrThrow(R.styleable.AppBar_titleTextAppearance))
        }
    }

    private fun createTextView(themeId: Int = 0): AppCompatTextView {
        return AppCompatTextView(if (themeId != 0) {
            ContextThemeWrapper(context, themeId)
        } else {
            context
        }).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
                hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
            }

            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            isSingleLine = true
            maxLines = 1

            this@AppBar.addView(this)
        }
    }

    private fun createButton(): AppCompatTextView {
        return createTextView(buttonsStyleId).apply {
            setOnClickListener(this@AppBar)

            gravity = Gravity.CENTER
            isFocusable = true
            isClickable = true
        }
    }

    private fun configureButton(button: AppCompatTextView, param: AppBarButtonParam) = button.let {
        if (param.iconRes != NOT_USED_PARAMETER) {
            it.setCompoundDrawablesWithIntrinsicBounds(param.iconRes, 0, 0, 0)
        } else {
            it.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        if (param.textRes != NOT_USED_PARAMETER) {
            it.setText(param.textRes)
        } else {
            it.text = null
        }

        it.isEnabled = param.isEnabled
        it.tag = param.tag
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(titleTextView, widthMeasureSpec, heightMeasureSpec)

        var freeWidth = MeasureSpec.getSize(widthMeasureSpec) - titleTextView.measuredWidth - paddingRight - paddingLeft

        buttonsAtLeft.forEach {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            freeWidth -= it.measuredWidth
        }

        buttonsAtRight.forEach {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            freeWidth -= it.measuredWidth
        }

        if (viewAtRight != null) {
            if (buttonsAtRight.isNotEmpty()) {
                freeWidth -= marginBetweenViewsAndButtons
            }

            val widthSpec = MeasureSpec.makeMeasureSpec(freeWidth, MeasureSpec.AT_MOST)

            measureChild(viewAtRight, widthSpec, heightMeasureSpec)
            freeWidth -= viewAtRight!!.measuredWidth
        }

        if (viewAtLeft != null) {
            if (buttonsAtLeft.isNotEmpty()) {
                freeWidth -= marginBetweenViewsAndButtons
            }

            val widthSpec = MeasureSpec.makeMeasureSpec(freeWidth, MeasureSpec.AT_MOST)

            measureChild(viewAtLeft, widthSpec, heightMeasureSpec)
            freeWidth -= viewAtLeft!!.measuredWidth
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), minimumHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!titleTextView.text.isNullOrEmpty()) {
            val leftBorder = measuredWidth / 2 - titleTextView.measuredWidth / 2
            val rightBorder = leftBorder + titleTextView.measuredWidth
            val topBorder = measuredHeight / 2 - titleTextView.measuredHeight / 2
            val bottomBorder = topBorder + titleTextView.measuredHeight

            titleTextView.lazyLayout(leftBorder, topBorder, rightBorder, bottomBorder)
        } else {
            titleTextView.lazyLayout(0, 0, 0, 0)
        }

        var leftBorderLeftButton = paddingLeft
        var rightBorderLeftButton = leftBorderLeftButton
        var rightBorderRightButton = measuredWidth - paddingRight
        var leftBorderRightButton = rightBorderRightButton
        val firstButton = buttonsAtLeft.firstOrNull() ?: buttonsAtRight.firstOrNull()

        if (firstButton != null) {
            val buttonsTopBorder = measuredHeight / 2 - firstButton.measuredHeight / 2
            val buttonsBottomBorder = buttonsTopBorder + firstButton.measuredHeight

            buttonsAtLeft.forEach {
                rightBorderLeftButton = leftBorderLeftButton + it.measuredWidth
                it.lazyLayout(leftBorderLeftButton, buttonsTopBorder, rightBorderLeftButton, buttonsBottomBorder)
                leftBorderLeftButton = rightBorderLeftButton
            }

            buttonsAtRight.forEach {
                leftBorderRightButton = rightBorderRightButton - it.measuredWidth
                it.lazyLayout(leftBorderRightButton, buttonsTopBorder, rightBorderRightButton, buttonsBottomBorder)
                rightBorderRightButton = leftBorderRightButton
            }
        }

        if (viewAtLeft != null) {
            if (buttonsAtLeft.isNotEmpty()) {
                rightBorderLeftButton += marginBetweenViewsAndButtons
            }

            val topBorder = measuredHeight / 2 - viewAtLeft!!.measuredHeight / 2
            val bottomBorder = topBorder + viewAtLeft!!.measuredHeight

            viewAtLeft!!.lazyLayout(rightBorderLeftButton, topBorder, rightBorderLeftButton + viewAtLeft!!.measuredWidth, bottomBorder)
        }

        if (viewAtRight != null) {
            if (buttonsAtRight.isNotEmpty()) {
                leftBorderRightButton -= marginBetweenViewsAndButtons
            }

            val topBorder = measuredHeight / 2 - viewAtRight!!.measuredHeight / 2
            val bottomBorder = topBorder + viewAtRight!!.measuredHeight

            viewAtRight!!.lazyLayout(leftBorderRightButton - viewAtRight!!.measuredWidth, topBorder, leftBorderRightButton, bottomBorder)
        }
    }

    override fun onClick(view: View) {
        callback?.invoke(view.tag as Int)
    }
}