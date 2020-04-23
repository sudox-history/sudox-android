package ru.sudox.design.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.use
import androidx.core.widget.TextViewCompat

class BottomLabeledImageButton : AppCompatTextView {

    var iconDrawable: Drawable?
        get() = compoundDrawables[1]
        set(value) {
            setCompoundDrawablesWithIntrinsicBounds(null, value, null, null)
        }

    var iconTint: Int
        get() = compoundDrawableTintList.defaultColor
        set(value) {
            TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(value))
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.bottomLabeledImageButtonStyle)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.BottomLabeledImageButton, defStyleAttr, 0).use {
            iconTint = it.getColorOrThrow(R.styleable.BottomLabeledImageButton_iconTint)
        }
    }
}