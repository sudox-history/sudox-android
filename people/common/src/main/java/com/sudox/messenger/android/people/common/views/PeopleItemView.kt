package com.sudox.messenger.android.people.common.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.use
import com.sudox.design.imagebutton.ImageButton
import com.sudox.design.imageview.CircleImageView
import com.sudox.messenger.android.people.common.R
import com.sudox.messenger.android.people.common.vos.PeopleVO

class PeopleItemView : ViewGroup {

    var activeTextColor = 0
        set(value) {
            field = value
            invalidate()
        }

    var inactiveTextColor = 0
        set(value) {
            field = value
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
                    tag = it.second
                    addView(this)
                }
            }

            if (value != null) {
                nameTextView.text = value.userName
                statusTextView.text = value.getStatusMessage(context)

            }

            field = value
            requestLayout()
            invalidate()
        }

    private var statusTextView = AppCompatTextView(context).apply { addView(this) }
    private var photoImageView = CircleImageView(context).apply { addView(this) }
    private var nameTextView = AppCompatTextView(context).apply { addView(this) }
    private var buttonsViews: List<ImageButton>? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.peopleItemViewStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.PeopleItemView, defStyleAttr, 0).use {

        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

    }
}