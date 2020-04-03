package com.sudox.design.codeedittext

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import com.sudox.design.edittext.BasicEditText

class CodeDigitEditText : BasicEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onRestoreInstanceState(state: Parcelable?) {
        (parent as? CodeEditText)?.let {
            it.isPositioningEnabled = false
            super.onRestoreInstanceState(state)
            it.isPositioningEnabled = true
        }
    }
}