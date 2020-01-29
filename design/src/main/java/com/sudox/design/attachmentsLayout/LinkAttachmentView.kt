package com.sudox.design.attachmentsLayout

import android.content.Context
import android.util.AttributeSet
import android.view.View

class LinkAttachmentView : View {

    private var vo: LinkAttachmentView? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setVO(vo: LinkAttachmentView) {
        this.vo = vo

        requestLayout()
        invalidate()
    }
}