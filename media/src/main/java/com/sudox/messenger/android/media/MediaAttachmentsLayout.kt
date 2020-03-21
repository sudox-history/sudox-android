package com.sudox.messenger.android.media

import android.content.Context
import android.util.AttributeSet
import com.sudox.design.mityushkinlayout.MityushkinLayout
import com.sudox.messenger.android.media.vos.MediaAttachmentVO

class MediaAttachmentsLayout : MityushkinLayout {

    var vos: ArrayList<MediaAttachmentVO>? = null
        set(value) {
            removeAllViewsInLayout()

            if (value != null) {
                val isLayoutDependsFromChildSize = adapter!!.getTemplate(value.size)!!.dependsFromChildSize

                for (i in 0 until value.size) {
                    val vo = value[i]

                    if (isLayoutDependsFromChildSize) {
                        addView(vo.getView(context), -1, LayoutParams(vo.width, vo.height))
                    } else {
                        addView(vo.getView(context), -1)
                    }
                }
            }

            field = value
            requestLayout()
            invalidate()
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}