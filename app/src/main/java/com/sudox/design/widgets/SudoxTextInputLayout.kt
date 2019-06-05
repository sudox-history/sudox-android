package com.sudox.design.widgets

import android.content.Context
import android.os.Build
import com.google.android.material.textfield.TextInputLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

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

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        // Fix SA-47 issue (https://sudox.myjetbrains.com/youtrack/issue/SA-47)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && child is EditText) {
            editText?.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }

        // Super!
        super.addView(child, index, params)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()

        // Reset color of line
        editText?.background?.clearColorFilter()
    }
}