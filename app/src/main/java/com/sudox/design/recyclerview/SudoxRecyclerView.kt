package com.sudox.design.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class SudoxRecyclerView : RecyclerView {

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init { }
}