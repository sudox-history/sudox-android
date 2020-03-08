package com.sudox.messenger.android.people.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import com.sudox.messenger.android.people.common.R

class VerticalPeopleItemView : HorizontalPeopleItemView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.verticalPeopleItemViewStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        nameTextView.gravity = Gravity.CENTER_HORIZONTAL
        statusTextView.gravity = Gravity.CENTER_HORIZONTAL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(nameTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(statusTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(photoImageView, widthMeasureSpec, heightMeasureSpec)

        buttonsViews?.forEach {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
        }

        val textsWidthSpec = MeasureSpec.makeMeasureSpec(minimumWidth, MeasureSpec.EXACTLY)

        measureChild(nameTextView, textsWidthSpec, heightMeasureSpec)
        measureChild(statusTextView, textsWidthSpec, heightMeasureSpec)

        setMeasuredDimension(minimumWidth, minimumHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var buttonsRightBorder = measuredWidth - paddingRight
        var buttonsLeftBorder: Int

        buttonsViews?.forEach {
            val topBorder = paddingTop
            val bottomBorder = topBorder + it.measuredHeight

            buttonsLeftBorder = buttonsRightBorder - it.measuredWidth
            it.layout(buttonsLeftBorder, topBorder, buttonsRightBorder, bottomBorder)
            buttonsRightBorder = buttonsLeftBorder - marginBetweenButtons
        }

        val needHeight = photoImageView.measuredHeight +
                marginBetweenAvatarAndTexts +
                nameTextView.measuredHeight +
                marginBetweenNameAndStatus +
                statusTextView.measuredHeight

        val photoTopBorder = measuredHeight / 2 - needHeight / 2
        val photoBottomBorder = photoTopBorder + photoImageView.measuredHeight
        val photoLeftBorder = measuredWidth / 2 - photoImageView.measuredWidth / 2
        val photoRightBorder = photoLeftBorder + photoImageView.measuredWidth

        photoImageView.layout(photoLeftBorder, photoTopBorder, photoRightBorder, photoBottomBorder)

        val nameTopBorder = photoBottomBorder + marginBetweenAvatarAndTexts
        val nameBottomBorder = nameTopBorder + nameTextView.measuredHeight
        val textsLeftBorder = paddingLeft
        val textsRightBorder = measuredWidth - paddingRight

        nameTextView.layout(textsLeftBorder, nameTopBorder, textsRightBorder, nameBottomBorder)

        val statusTopBorder = nameBottomBorder + marginBetweenNameAndStatus
        val statusBottomBorder = statusTopBorder + statusTextView.measuredHeight

        statusTextView.layout(textsLeftBorder, statusTopBorder, textsRightBorder, statusBottomBorder)
    }
}