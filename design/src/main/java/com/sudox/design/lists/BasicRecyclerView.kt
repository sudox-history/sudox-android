package com.sudox.design.lists

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView

open class BasicRecyclerView : RecyclerView {

    internal var initialPaddingRight = 0
    internal var initialPaddingLeft = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init {
        initialPaddingRight = paddingRight
        initialPaddingLeft = paddingLeft

        updatePadding(left = 0, right = 0)
    }
}