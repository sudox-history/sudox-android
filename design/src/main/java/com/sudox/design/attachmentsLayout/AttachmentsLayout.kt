package com.sudox.design.attachmentsLayout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayout
import com.sudox.design.attachmentsLayout.vos.LinkAttachmentVO

class AttachmentsLayout : ViewGroup {

    private var mediaLayout: FlexboxLayout? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}