package com.sudox.messenger.android.news.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.flexbox.FlexboxLayout
import com.sudox.design.circleImageView.CircleImageView

class NewsItemView : ViewGroup {

    private val publisherNameTextView = AppCompatTextView(context).apply { addView(this) }
    private val publisherPhotoImageView = CircleImageView(context).apply { addView(this) }
    private val publishTimeTextView = AppCompatTextView(context).apply { addView(this) }
    private val contentTextView = AppCompatTextView(context).apply { addView(this) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

    }

    fun setPublisherName(name: String) {
        publisherNameTextView.text = name
    }

    fun setPublisherPhoto(bitmap: Bitmap?) {
        publisherPhotoImageView.setImageBitmap(bitmap)
    }

    fun setPublisherPhoto(drawable: Drawable?) {
        publisherPhotoImageView.setImageDrawable(drawable)
    }

    fun setPublishTime(time: Long) {
        // TODO: Replace to formatTime()
        publishTimeTextView.text = time.toString()
    }

    fun setContent(spannable: Spannable?) {
        contentTextView.text = spannable
    }

    fun setContext(text: String?) {
        // TODO: Replace to formatContent()
        contentTextView.text = text
    }
}