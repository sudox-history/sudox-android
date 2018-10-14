package com.sudox.android.ui.views.toolbar.expanded

import android.content.Context
import android.util.AttributeSet
import com.sudox.android.R

class ContactAddExpandedView : ExpandedView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.expanded_contact_add_view, this)
    }

    override fun clear() {
        
    }
}