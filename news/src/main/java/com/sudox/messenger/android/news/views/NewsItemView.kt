package com.sudox.messenger.android.news.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.sudox.design.circleImageView.CircleImageView

class NewsItemView : ViewGroup {

    private val publisherNameTextView = AppCompatTextView(context).apply { addView(this) }
    private val publisherPhotoImageView = CircleImageView(context).apply { addView(this) }
    private val publishTimeTextView = CircleImageView(context).apply { addView(this) }
    private val contentTextView = AppCompatTextView(context).apply { addView(this) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

    }
}