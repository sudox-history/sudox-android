package com.sudox.messenger.android.images.views

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import com.sudox.design.imageview.CircleImageView
import com.sudox.messenger.android.images.ImageLoadingListener

open class LoadableCircleImageView : CircleImageView, ImageLoadingListener {

    private var requestedImageId = 0L

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLoadingCompleted(bitmap: Bitmap) {
        this.bitmap = bitmap
    }

    override fun onLoadingStarted() {

    }

    override fun getRequestedImageId(): Long {
        return requestedImageId
    }

    override fun setRequestedImageId(id: Long) {
        this.requestedImageId = id
    }
}