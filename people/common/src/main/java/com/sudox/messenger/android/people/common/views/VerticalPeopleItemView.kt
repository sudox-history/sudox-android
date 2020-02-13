package com.sudox.messenger.android.people.common.views

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
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

class VerticalPeopleItemView : ViewGroup {

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

    var marginBetweenNameAndStatus = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenTopAndButton = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var marginBetweenRightAndButton = 0
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var vo: PeopleVO? = null
        set(value) {
            if (button != null) {
                removeView(button)
            }

            val pair = value?.getButtons()?.firstOrNull()

            button = if (pair != null) {
                ImageButton(ContextThemeWrapper(context, pair.first)).apply {
                    layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    tag = pair.second

                    addView(this)
                }
            } else {
                null
            }

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
        gravity = Gravity.CENTER_HORIZONTAL
        isSingleLine = true
        maxLines = 1

        addView(this)
    }

    private var statusTextView = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        ellipsize = TextUtils.TruncateAt.END
        gravity = Gravity.CENTER_HORIZONTAL
        isSingleLine = true
        maxLines = 1

        addView(this)
    }

    private var photoImageView = AvatarImageView(context).apply { addView(this) }
    private var button: ImageButton? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.verticalPeopleItemViewStyle)
    
    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.VerticalPeopleItemView, defStyleAttr, 0).use {
            setTextAppearance(nameTextView, it.getResourceIdOrThrow(R.styleable.VerticalPeopleItemView_nameTextAppearance))
            setTextAppearance(statusTextView, it.getResourceIdOrThrow(R.styleable.VerticalPeopleItemView_statusTextAppearance))

            photoImageView.layoutParams = LayoutParams(
                    it.getDimensionPixelSizeOrThrow(R.styleable.VerticalPeopleItemView_avatarWidth),
                    it.getDimensionPixelSizeOrThrow(R.styleable.VerticalPeopleItemView_avatarHeight)
            )

            activeStatusTextColor = it.getColorOrThrow(R.styleable.VerticalPeopleItemView_activeStatusTextColor)
            inactiveStatusTextColor = it.getColorOrThrow(R.styleable.VerticalPeopleItemView_inactiveStatusTextColor)
            marginBetweenAvatarAndTexts = it.getDimensionPixelSize(R.styleable.VerticalPeopleItemView_marginBetweenAvatarAndTexts, 0)
            marginBetweenNameAndStatus = it.getDimensionPixelSize(R.styleable.VerticalPeopleItemView_marginBetweenNameAndStatus, 0)
            marginBetweenTopAndButton = it.getDimensionPixelSize(R.styleable.VerticalPeopleItemView_marginBetweenTopAndButton, 0)
            marginBetweenRightAndButton = it.getDimensionPixelSize(R.styleable.VerticalPeopleItemView_marginBetweenRightAndButton, 0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(nameTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(statusTextView, widthMeasureSpec, heightMeasureSpec)
        measureChild(photoImageView, widthMeasureSpec, heightMeasureSpec)

        if (button != null) {
            measureChild(button, widthMeasureSpec, heightMeasureSpec)
        }

        val textsWidthSpec = MeasureSpec.makeMeasureSpec(minimumWidth, MeasureSpec.EXACTLY)

        measureChild(nameTextView, textsWidthSpec, heightMeasureSpec)
        measureChild(statusTextView, textsWidthSpec, heightMeasureSpec)

        val needWidth = minimumWidth + paddingRight + paddingLeft
        val needHeight = minimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(needWidth, needHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val photoTopBorder = paddingTop
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

        if (button != null) {
            val buttonTopBorder = marginBetweenTopAndButton
            val buttonBottomBorder = buttonTopBorder + button!!.measuredHeight
            val buttonRightBorder = measuredWidth - marginBetweenRightAndButton
            val buttonLeftBorder = buttonRightBorder - button!!.measuredWidth

            button!!.layout(buttonLeftBorder, buttonTopBorder, buttonRightBorder, buttonBottomBorder)
        }
    }
}