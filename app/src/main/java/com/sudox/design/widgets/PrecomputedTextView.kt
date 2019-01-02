package com.sudox.design.widgets

import android.content.Context
import android.support.v4.text.PrecomputedTextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.widget.TextView
import com.sudox.android.R

class PrecomputedTextView : TextView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (attrs != null) readAttrs(attrs)
    }

    private fun readAttrs(attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.PrecomputedTextView)
        val text = array.getText(R.styleable.PrecomputedTextView_text)

        // Set for showing
        if (text != null) installText(text)

        // Clean memory
        array.recycle()
    }

    fun installText(charSequence: CharSequence) {
        val params = TextViewCompat.getTextMetricsParams(this)
        val text = PrecomputedTextCompat.create(charSequence, params)

        // Start render!
        TextViewCompat.setPrecomputedText(this, text)
    }
}