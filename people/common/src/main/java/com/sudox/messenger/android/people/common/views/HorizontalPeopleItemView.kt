package com.sudox.messenger.android.people.common.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.Layout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.messenger.android.people.common.R
import com.sudox.messenger.android.people.common.vos.PeopleVO
import java.util.ArrayList
import kotlin.math.max

/**
 * Горизонтальная View для отображения информации о человеке.
 * Отображает базовую информацию (имя, онлайн и т.п.).
 */
open class HorizontalPeopleItemView : ViewGroup {

    var activeStatusTextColor = 0
        set(value) {
            field = value
            vo = vo // Updating config
        }

    var inactiveStatusTextColor = 0
        set(value) {
            field = value
            vo = vo // Updating config
        }

    var marginBetweenAvatarAndTexts = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenButtonsAndTexts = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenNameAndStatus = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenButtons = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var buttonsWidth: Int = 0
        set(value) {
            field = value
            vo = vo
        }

    var buttonsHeight: Int = 0
        set(value) {
            field = value
            vo = vo
        }

    var vo: PeopleVO? = null
        set(value) {
            val buttons = value?.getButtons()
            val needRemove = (buttonsViews?.size ?: 0) - (buttons?.size ?: 0)
            val needAdd = (buttons?.size ?: 0) - (buttonsViews?.size ?: 0)

            if (needRemove > 0) {
                repeat(needRemove) {
                    removeViewInLayout(with(buttonsViews!!) {
                        removeAt(lastIndex)
                    })
                }
            } else if (needAdd > 0) {
                if (buttonsViews == null) {
                    buttonsViews = ArrayList()
                }

                repeat(needAdd) {
                    buttonsViews!!.add(AppCompatImageButton(context).apply {
                        addViewInLayout(this, -1, LayoutParams(buttonsWidth, buttonsHeight))
                    })
                }
            }

            buttonsViews?.forEachIndexed { index, imageButton ->
                buttons!![index].let {
                    imageButton.tag = it.first
                    imageButton.layoutParams = LayoutParams(buttonsWidth, buttonsHeight)
                    imageButton.setImageResource(it.second)
                    ImageViewCompat.setImageTintList(imageButton, ColorStateList.valueOf(getColor(context, it.third)))
                }
            }

            nameTextView.text = value?.userName
            photoImageView.vo = value
            statusTextView.let {
                it.setTextColor(if (value?.isStatusAboutOnline() == true && value.isStatusActive()) {
                    activeStatusTextColor
                } else {
                    inactiveStatusTextColor
                })

                it.text = value?.getStatusMessage(context)
            }

            field = value
            requestLayout()
            invalidate()
        }

    internal var nameTextView = AppCompatTextView(context).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        ellipsize = TextUtils.TruncateAt.END
        isSingleLine = true
        maxLines = 1

        addView(this)
    }

    internal var statusTextView = AppCompatTextView(context).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
            hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
        }

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        ellipsize = TextUtils.TruncateAt.END
        isSingleLine = true
        maxLines = 1

        addView(this)
    }

    internal var photoImageView = AvatarImageView(context).apply { addView(this) }
    internal var buttonsViews: ArrayList<ImageButton>? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.horizontalPeopleItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.HorizontalPeopleItemView, defStyleAttr, 0).use {
            setTextAppearance(nameTextView, it.getResourceIdOrThrow(R.styleable.HorizontalPeopleItemView_nameTextAppearance))
            setTextAppearance(statusTextView, it.getResourceIdOrThrow(R.styleable.HorizontalPeopleItemView_statusTextAppearance))

            photoImageView.layoutParams = LayoutParams(
                    it.getDimensionPixelSizeOrThrow(R.styleable.HorizontalPeopleItemView_avatarWidth),
                    it.getDimensionPixelSizeOrThrow(R.styleable.HorizontalPeopleItemView_avatarHeight)
            )

            activeStatusTextColor = it.getColorOrThrow(R.styleable.HorizontalPeopleItemView_activeStatusTextColor)
            inactiveStatusTextColor = it.getColorOrThrow(R.styleable.HorizontalPeopleItemView_inactiveStatusTextColor)
            marginBetweenAvatarAndTexts = it.getDimensionPixelSize(R.styleable.HorizontalPeopleItemView_marginBetweenAvatarAndTexts, 0)
            marginBetweenNameAndStatus = it.getDimensionPixelSize(R.styleable.HorizontalPeopleItemView_marginBetweenNameAndStatus, 0)
            marginBetweenButtonsAndTexts = it.getDimensionPixelSize(R.styleable.HorizontalPeopleItemView_marginBetweenButtonsAndTexts, 0)
            marginBetweenButtons = it.getDimensionPixelSize(R.styleable.HorizontalPeopleItemView_marginBetweenButtons, 0)
            buttonsHeight = it.getDimensionPixelSizeOrThrow(R.styleable.HorizontalPeopleItemView_buttonsHeight)
            buttonsWidth = it.getDimensionPixelSizeOrThrow(R.styleable.HorizontalPeopleItemView_buttonsWidth)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(nameTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(photoImageView, widthMeasureSpec, heightMeasureSpec)
        measureChild(statusTextView, widthMeasureSpec, heightMeasureSpec)

        buttonsViews?.forEach {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
        }

        val needWidth = paddingLeft + photoImageView.measuredWidth + marginBetweenAvatarAndTexts + paddingRight +
                (buttonsViews?.sumBy {
                    it.measuredWidth
                } ?: 0)

        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val textBlockWidth = availableWidth - needWidth
        val textBlockWidthSpec = MeasureSpec.makeMeasureSpec(textBlockWidth, MeasureSpec.EXACTLY)

        nameTextView.measure(textBlockWidthSpec, heightMeasureSpec)
        statusTextView.measure(textBlockWidthSpec, heightMeasureSpec)

        val maxButtonHeight = buttonsViews?.maxBy { it.measuredHeight }?.measuredHeight ?: 0
        val textsHeightSum = nameTextView.measuredHeight + statusTextView.measuredHeight
        val needHeight = paddingTop + max(max(textsHeightSum, photoImageView.measuredHeight), maxButtonHeight) + paddingBottom

        setMeasuredDimension(availableWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var buttonsRightBorder = measuredWidth - paddingRight
        var buttonsLeftBorder: Int

        if (!buttonsViews.isNullOrEmpty()) {
            for (i in buttonsViews!!.size - 1 downTo 0) {
                buttonsViews!![i].let {
                    val topBorder = measuredHeight / 2 - it.measuredHeight / 2
                    val bottomBorder = topBorder + it.measuredHeight

                    buttonsLeftBorder = buttonsRightBorder - it.measuredWidth
                    it.layout(buttonsLeftBorder, topBorder, buttonsRightBorder, bottomBorder)
                    buttonsRightBorder = buttonsLeftBorder - marginBetweenButtons
                }
            }
        }

        val photoLeftBorder = paddingLeft
        val photoRightBorder = photoLeftBorder + photoImageView.measuredWidth
        val photoTopBorder = paddingTop
        val photoBottomBorder = photoTopBorder + photoImageView.measuredWidth

        photoImageView.layout(photoLeftBorder, photoTopBorder, photoRightBorder, photoBottomBorder)

        val nameBottomBorder = measuredHeight / 2 + marginBetweenNameAndStatus / 2
        val nameTopBorder = nameBottomBorder - nameTextView.measuredHeight
        val nameLeftBorder = photoRightBorder + marginBetweenAvatarAndTexts
        var nameRightBorder = nameLeftBorder + nameTextView.measuredWidth

        val statusTopBorder = measuredHeight / 2 - marginBetweenNameAndStatus / 2
        val statusBottomBorder = statusTopBorder + statusTextView.measuredHeight
        val statusLeftBorder = photoRightBorder + marginBetweenAvatarAndTexts
        var statusRightBorder = statusLeftBorder + statusTextView.measuredWidth

        if (!buttonsViews.isNullOrEmpty()) {
            nameRightBorder -= marginBetweenButtonsAndTexts
            statusRightBorder -= marginBetweenButtonsAndTexts
        }

        nameTextView.layout(nameLeftBorder, nameTopBorder, nameRightBorder, nameBottomBorder)
        statusTextView.layout(statusLeftBorder, statusTopBorder, statusRightBorder, statusBottomBorder)
    }
}