package com.sudox.messenger.android.people.common.views

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.sudox.design.imagebutton.ImageButton
import com.sudox.messenger.android.people.common.R
import com.sudox.messenger.android.people.common.vos.PeopleVO
import kotlin.math.max

/**
 * Горизонтальная View для отображения информации о человеке.
 * Отображает базовую информацию (имя, онлайн и т.п.).
 */
class HorizontalPeopleItemView : ViewGroup {

    var activeStatusTextColor = 0
        set(value) {
            field = value
            vo = vo // Updating config
            invalidate()
        }

    var inactiveStatusTextColor = 0
        set(value) {
            field = value
            vo = vo // Updating config
            invalidate()
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

    var vo: PeopleVO? = null
        set(value) {
            buttonsViews?.forEach { removeView(it) }
            buttonsViews = value?.getButtons()?.map {
                ImageButton(ContextThemeWrapper(context, it.first)).apply {
                    layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    tag = it.second

                    addView(this)
                }
            }?.asReversed()

            nameTextView.text = value?.userName
            statusTextView.text = value?.getStatusMessage(context)
            statusTextView.setTextColor(if (value?.isStatusAboutOnline() == true && value.isStatusActive()) {
                activeStatusTextColor
            } else {
                inactiveStatusTextColor
            })

            photoImageView.vo = value

            field = value
            requestLayout()
            invalidate()
        }

    private var nameTextView = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        ellipsize = TextUtils.TruncateAt.END
        isSingleLine = true
        maxLines = 1

        addView(this)
    }

    private var statusTextView = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        ellipsize = TextUtils.TruncateAt.END
        isSingleLine = true
        maxLines = 1

        addView(this)
    }

    private var photoImageView = AvatarImageView(context).apply { addView(this) }
    private var buttonsViews: List<ImageButton>? = null

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

        measureChild(nameTextView, textBlockWidthSpec, heightMeasureSpec)
        measureChild(statusTextView, textBlockWidthSpec, heightMeasureSpec)

        val maxButtonHeight = buttonsViews?.maxBy { it.measuredHeight }?.measuredHeight ?: 0
        val textsHeightSum = nameTextView.measuredHeight + statusTextView.measuredHeight
        val needHeight = paddingTop + max(max(textsHeightSum, photoImageView.measuredHeight), maxButtonHeight)

        setMeasuredDimension(availableWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var buttonsRightBorder = measuredWidth - paddingRight
        var buttonsLeftBorder: Int

        buttonsViews?.forEach {
            val topBorder = measuredHeight / 2 - it.measuredHeight / 2
            val bottomBorder = topBorder + it.measuredHeight

            buttonsLeftBorder = buttonsRightBorder - it.measuredWidth
            it.layout(buttonsLeftBorder, topBorder, buttonsRightBorder, bottomBorder)
            buttonsRightBorder = buttonsLeftBorder - marginBetweenButtons
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

        if (buttonsViews?.isNotEmpty() == true) {
            nameRightBorder -= marginBetweenButtonsAndTexts
            statusRightBorder -= marginBetweenButtonsAndTexts
        }

        nameTextView.layout(nameLeftBorder, nameTopBorder, nameRightBorder, nameBottomBorder)
        statusTextView.layout(statusLeftBorder, statusTopBorder, statusRightBorder, statusBottomBorder)
    }
}