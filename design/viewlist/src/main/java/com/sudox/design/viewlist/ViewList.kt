package com.sudox.design.viewlist

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView

class ViewList : RecyclerView {

    internal var footerTextAppearance = 0
    internal var initialPaddingRight = 0
    internal var initialPaddingLeft = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.viewListStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        context.obtainStyledAttributes(attrs, R.styleable.ViewList, defStyle, 0).use {
            footerTextAppearance = it.getResourceIdOrThrow(R.styleable.ViewList_footerTextAppearance)
        }

        initialPaddingRight = paddingRight
        initialPaddingLeft = paddingLeft

        updatePadding(left = 0, right = 0)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)

        if (adapter != null && adapter is ViewListAdapter<*>) {
            addItemDecoration(ViewListDecorator(adapter, this))
        } else {
            removeItemDecoration(getItemDecorationAt(0))
        }
    }
}