package com.sudox.design.widgets

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.sudox.android.R

class SheetButton : LinearLayout {

    private val iconView: View by lazy {
        View(context!!).apply {
            layoutParams = LinearLayout.LayoutParams(
                    (16 * resources.displayMetrics.density).toInt(),
                    (16 * resources.displayMetrics.density).toInt()).apply {

                // Отступ слева
                marginStart = (25 * resources.displayMetrics.density).toInt()
                gravity = Gravity.CENTER_VERTICAL
            }
        }
    }

    private val titleTextView by lazy {
        PrecomputedTextView(context!!).apply {
            textSize = 12F
            layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT).apply {

                // Отступ слева
                marginStart = (24 * resources.displayMetrics.density).toInt()
                gravity = Gravity.CENTER_VERTICAL
            }
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, (35 * resources.displayMetrics.density).toInt())
        isClickable = true
        isFocusable = true

        // Read attrs
        if (attrs != null) readAttrs(attrs)

        // Add ripple effect
        val styledAttributes = context.obtainStyledAttributes(IntArray(1) {
            android.R.attr.selectableItemBackground
        })

        // Some optimizations for Marshmallow (6.0) and higher devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            foreground = styledAttributes.getDrawable(0)
            setBackgroundColor(Color.parseColor("#1A1E28"))

            // Simple hierarchy than for Lollipop devices
            addView(iconView)
            addView(titleTextView)
        } else {
            setBackgroundColor(Color.parseColor("#1A1E28"))

            // Warning: Hard hierarchy! Please, ignore the dinosaurs
            addView(LinearLayout(context).apply {
                background = styledAttributes.getDrawable(0)
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, (35 * resources.displayMetrics.density).toInt())

                // "Oh blyat ..." - says TheMax
                addView(iconView)
                addView(titleTextView)
            })
        }

        // Clean memory
        styledAttributes.recycle()
    }

    private fun readAttrs(attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SheetButton)
        val iconDrawable = array.getDrawable(R.styleable.SheetButton_buttonIcon)
        val text = array.getString(R.styleable.SheetButton_buttonText)

        // Bind parameters
        if (iconDrawable != null) iconView.background = iconDrawable
        if (text != null) titleTextView.installText(text)

        // Clean memory
        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Get params
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (35 * resources.displayMetrics.density).toInt()

        // Bind height
        setMeasuredDimension(width, height)
    }
}