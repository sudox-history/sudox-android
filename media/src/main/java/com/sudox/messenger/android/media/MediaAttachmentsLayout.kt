package com.sudox.messenger.android.media

import android.content.Context
import android.util.AttributeSet
import com.sudox.design.mityushkinlayout.MityushkinLayout
import com.sudox.messenger.android.media.vos.MediaAttachmentVO

class MediaAttachmentsLayout : MityushkinLayout {

    var vos: ArrayList<MediaAttachmentVO>? = null
        set(value) {
            if (field != null) {
                for (i in 0 until childCount) {
                    removeViewInLayout(getChildAt(i).apply {
                        field!![i].unbindView(this)
                    })
                }
            }

            removeAllViewsInLayout() // TODO: Реализовать переиспользование уже добаленных View

            if (value != null) {
                val isLayoutDependsFromChildSize = adapter!!.getTemplate(value.size)!!.dependsFromChildSize

                for (i in 0 until value.size) {
                    val vo = value[i]
                    val view = vo.getView(context)

                    if (isLayoutDependsFromChildSize) {
                        addView(view, -1, LayoutParams(vo.width, vo.height))
                    } else {
                        addView(view)
                    }

                    vo.bindView(view)
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