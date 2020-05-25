package ru.sudox.android.messages.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import ru.sudox.android.messages.R

class MessageItemView : ViewGroup {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.messageItemViewStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        TODO("Not yet implemented")
    }
}