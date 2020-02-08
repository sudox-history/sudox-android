package com.sudox.messenger.android.people.common.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.sudox.design.imageview.CircleImageView
import com.sudox.messenger.android.images.views.LoadableCircleImageView

class AvatarImageView : LoadableCircleImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
    }
}