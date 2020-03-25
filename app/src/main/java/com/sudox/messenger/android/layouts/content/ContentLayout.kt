package com.sudox.messenger.android.layouts.content

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.NestedScrollView
import com.sudox.messenger.android.layouts.child.AppLayoutChild

class ContentLayout : NestedScrollView {

    val layoutChild = AppLayoutChild(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        id = View.generateViewId()

        this@ContentLayout.addView(this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->

        }

        isFillViewport = true
    }

    override fun onSaveInstanceState(): Parcelable? {
        return ContentLayoutState(super.onSaveInstanceState()!!).apply {
            readFromView(this@ContentLayout)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as ContentLayoutState).apply {
            super.onRestoreInstanceState(superState)
            writeToView(this@ContentLayout)
        }
    }
}