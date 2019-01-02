package com.sudox.design.widgets

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class SudoxTextInputLayout : TextInputLayout {

    private val initialHint by lazy {
        TextInputLayout::class.java
                .getDeclaredField("originalHint")
                .apply { isAccessible = true }
                .get(this) as CharSequence?
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setError(errorText: CharSequence?) {
        super.setError(errorText)

        // Update hint
        hint = if (!TextUtils.isEmpty(errorText)) {
            errorText
        } else {
            initialHint
        }

        // Hide bottom error text
        if (childCount == 2)
            (getChildAt(1) as ViewGroup)
                    .getChildAt(0)
                    .visibility = View.GONE

        // Reset color of line
        editText?.background?.clearColorFilter()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()

        // Reset color of line
        editText?.background?.clearColorFilter()
    }
}