package com.sudox.messenger.android.images.views

import android.content.Context
import android.util.AttributeSet
import com.sudox.design.imageview.CircleImageView
import com.sudox.design.imageview.ImageView
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
open class LoadableCircleImageView : CircleImageView, LoadableImageView {

    override var showingImageId: Long = IMAGE_NOT_SHOWING_ID

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun getInstance(): ImageView {
        return this
    }
}