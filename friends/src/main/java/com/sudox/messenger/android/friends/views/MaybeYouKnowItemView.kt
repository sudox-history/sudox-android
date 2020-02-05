package com.sudox.messenger.android.friends.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.sudox.messenger.android.friends.R

class MaybeYouKnowItemView : ViewGroup {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.maybeYouKnowItemViewStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}