package com.sudox.android.ui.views.toolbar.expanded

import android.content.Context
import android.util.AttributeSet
import com.sudox.android.R

class ContactFoundedExpandedView: ExpandedView {

    constructor(context: Context) : super(context, false)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.founded_contact_layout, this)
    }

    override fun clear() {
    }
}