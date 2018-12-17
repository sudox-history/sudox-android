package com.sudox.design.avatar

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.graphics.drawable.shapes.OvalShape
import android.support.v7.widget.AppCompatTextView
import android.util.TypedValue
import com.sudox.design.helpers.getTwoFirstLetters


class TextAvatarView : FrameLayout {

    private val text by lazy {
        AppCompatTextView(context).apply {
            layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER)
        }
    }

    private val background by lazy {
        ImageView(context).apply {
            layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        addView(background)
        addView(text.apply {
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        })
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (!changed) return

        // Shape ...
        setBackground(ShapeDrawable(OvalShape()).apply {
            intrinsicHeight = measuredHeight
            intrinsicWidth = measuredWidth
            bounds = Rect(measuredHeight / 2, measuredWidth / 2, measuredHeight / 2, measuredWidth / 2)
            paint.color = Color.WHITE
        })

        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.min(measuredHeight, measuredWidth) * 0.3F)
    }

    fun bindLetters(textForShowing: String) {
        var letters = textForShowing.getTwoFirstLetters()

        // Update only if needed
        if (text.text.toString() != letters) {
            text.text = letters
        }
    }
}