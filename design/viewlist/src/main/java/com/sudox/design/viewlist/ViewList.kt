package com.sudox.design.viewlist

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.recyclerview.widget.RecyclerView

class ViewList : RecyclerView {

    internal var headerTextAppearance = 0
    internal var footerTextAppearance = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.viewListStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        context.obtainStyledAttributes(attrs, R.styleable.ViewList, defStyle, 0).use {
            headerTextAppearance = it.getResourceIdOrThrow(R.styleable.ViewList_headerTextAppearance)
            footerTextAppearance = it.getResourceIdOrThrow(R.styleable.ViewList_footerTextAppearance)
        }
    }
}