package com.sudox.android.ui.views

import android.content.Context
import android.util.AttributeSet
import com.sudox.android.R
import com.sudox.android.ui.views.toolbar.expanded.ExpandedView

class ErrorExpandedView : ExpandedView {

    constructor(context: Context) : super(context, false)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.error_text_view, this)
    }

    override fun clear() {}
}