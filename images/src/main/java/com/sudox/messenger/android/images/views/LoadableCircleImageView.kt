package com.sudox.messenger.android.images.views

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import com.sudox.design.imageview.CircleImageView
import com.sudox.messenger.android.images.ImageLoadingListener
import com.sudox.messenger.android.images.NOT_REQUESTED_IMAGE_ID
import com.sudox.messenger.android.images.storages.stopImageLoading

open class LoadableCircleImageView : CircleImageView, ImageLoadingListener {

    override var requestedImageId: Long = NOT_REQUESTED_IMAGE_ID

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDetachedFromWindow() {
        stopImageLoading(this, requestedImageId)
        super.onDetachedFromWindow()
    }

    override fun onLoadingCompleted(bitmap: Bitmap) {
        this.bitmap = bitmap
    }

    override fun onLoadingStarted() {
        this.bitmap = null
    }

    override fun onLoadingStopped() {
        this.bitmap = null
    }
}