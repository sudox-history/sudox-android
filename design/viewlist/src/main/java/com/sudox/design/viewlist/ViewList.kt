package com.sudox.design.viewlist

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class ViewList : RecyclerView {

    internal var headerTextAppearance = R.style.TextAppearance_AppCompat
    internal var footerTextAppearance = R.style.TextAppearance_AppCompat

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
}