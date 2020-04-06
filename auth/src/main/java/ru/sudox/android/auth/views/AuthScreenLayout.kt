package ru.sudox.android.auth.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Parcelable
import android.text.Layout
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat
import androidx.core.widget.TextViewCompat.setTextAppearance
import ru.sudox.design.saveableview.SaveableViewGroup
import ru.sudox.android.auth.R
import ru.sudox.android.auth.vos.AuthScreenVO
import kotlin.math.max

class AuthScreenLayout : SaveableViewGroup<AuthScreenLayout, AuthScreenLayoutState> {

    var marginBetweenChildren = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenDescriptionAndChild = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenTitleAndDescription = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenIconAndDescription
        get() = descriptionTextView.compoundDrawablePadding
        set(value) {
            descriptionTextView.compoundDrawablePadding = value
            requestLayout()
            invalidate()
        }

    var vo: AuthScreenVO? = null
        set(value) {
            removeAllViewsInLayout()

            if (value != null) {
                addView(titleTextView.apply {
                    text = value.getTitle(context)
                })

                val descriptionTriple = value.getDescription(context)

                addView(descriptionTextView.apply {
                    setCompoundDrawablesWithIntrinsicBounds(descriptionTriple.first, 0, 0, 0)

                    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(
                            ContextCompat.getColor(context, descriptionTriple.second)
                    ))

                    text = descriptionTriple.third
                })

                childViews = value.getChildViews(context)
            }

            field = value
            requestLayout()
            invalidate()
        }

    private var titleTextView = AppCompatTextView(context).apply {
        isSingleLine = true
        maxLines = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }
    }

    private var descriptionTextView = AppCompatTextView(context).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }
    }

    internal var childViews: Array<View>? = null
        set(value) {
            value?.forEach {
                it.id = View.generateViewId()
                it.layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                )

                addView(it)
            }

            field = value
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.authScreenLayoutStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.AuthScreenLayout, defStyleAttr, 0).use {
            marginBetweenChildren = it.getDimensionPixelSize(R.styleable.AuthScreenLayout_marginBetweenChildren, 0)
            marginBetweenDescriptionAndChild = it.getDimensionPixelSize(R.styleable.AuthScreenLayout_marginBetweenDescriptionAndChild, 0)
            marginBetweenTitleAndDescription = it.getDimensionPixelSize(R.styleable.AuthScreenLayout_marginBetweenTitleAndDescription, 0)
            marginBetweenTitleAndDescription = it.getDimensionPixelSize(R.styleable.AuthScreenLayout_marginBetweenTitleAndDescription, 0)
            marginBetweenIconAndDescription = it.getDimensionPixelSize(R.styleable.AuthScreenLayout_marginBetweenIconAndDescription, 0)

            setTextAppearance(titleTextView, it.getResourceIdOrThrow(R.styleable.AuthScreenLayout_titleTextAppearance))
            setTextAppearance(descriptionTextView, it.getResourceIdOrThrow(R.styleable.AuthScreenLayout_descriptionTextAppearance))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(titleTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(descriptionTextView, widthMeasureSpec, heightMeasureSpec)

        val childHeight = childViews?.sumBy {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            it.measuredHeight
        } ?: 0

        val needWidth = MeasureSpec.getSize(widthMeasureSpec)
        val needHeight = paddingTop +
                titleTextView.measuredHeight +
                marginBetweenTitleAndDescription +
                descriptionTextView.measuredHeight +
                marginBetweenDescriptionAndChild +
                (max((childViews?.size ?: 0) - 1, 0) * marginBetweenChildren) +
                childHeight +
                paddingBottom

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val leftBorder = paddingLeft

        val titleTopBorder = paddingTop
        val titleRightBorder = leftBorder + titleTextView.measuredWidth
        val titleBottomBorder = titleTopBorder + titleTextView.measuredHeight

        titleTextView.layout(leftBorder, titleTopBorder, titleRightBorder, titleBottomBorder)

        val descriptionTopBorder = titleBottomBorder + marginBetweenTitleAndDescription
        val descriptionBottomBorder = descriptionTopBorder + descriptionTextView.measuredHeight
        val descriptionRightBorder = leftBorder + descriptionTextView.measuredWidth

        descriptionTextView.layout(leftBorder, descriptionTopBorder, descriptionRightBorder, descriptionBottomBorder)

        var topBorder = descriptionBottomBorder + marginBetweenDescriptionAndChild
        var bottomBorder: Int

        childViews?.forEach {
            val rightBorder = leftBorder + it.measuredWidth

            bottomBorder = topBorder + it.measuredHeight
            it.layout(leftBorder, topBorder, rightBorder, bottomBorder)
            topBorder = bottomBorder + marginBetweenChildren
        }
    }

    override fun createStateInstance(superState: Parcelable): AuthScreenLayoutState {
        return AuthScreenLayoutState(superState)
    }
}