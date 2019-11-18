package com.sudox.design.codeEditText

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.text.InputType
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

@SuppressLint("ViewConstructor")
class CodeDigitEditText(
        context: Context,
        val codeEditText: CodeEditText,
        index: Int
) : AppCompatEditText(context) {

    init {
        codeEditText.addView(this)

        id = View.generateViewId()
        gravity = Gravity.CENTER
        inputType = InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL or
                InputType.TYPE_NUMBER_FLAG_SIGNED

        CodeTextWatcher(this, index, codeEditText).apply {
            addTextChangedListener(this)
            setOnKeyListener(this)
        }

        background = background.mutate()
        isSingleLine = true
        maxLines = 1
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        codeEditText.isPositioningEnabled = false
        super.onRestoreInstanceState(state)
        codeEditText.isPositioningEnabled = true
    }
}