package ru.sudox.android.core.ui.toolbar

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.use
import androidx.core.view.updateLayoutParams
import ru.sudox.android.core.ui.R

/**
 * Измененный SearchView
 *
 * 1) Исправлены отступы
 * 2) Исправлен RippleEffect
 */
class SearchView : SearchView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.SearchView).use {
            if (it.getBoolean(R.styleable.SearchView_expandAutomatically, false)) {
                onActionViewExpanded()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(minimumHeight, MeasureSpec.EXACTLY))
    }

    init {
        val searchButton = findViewById<View>(R.id.search_button)

        findViewById<View>(R.id.search_src_text).setPadding(0, 0, 0, 0)
        findViewById<ImageView>(R.id.search_close_btn).apply {
            setPaddingRelative(searchButton.paddingStart, 0, searchButton.paddingEnd, 0)
            setBackgroundResource(with(TypedValue()) {
                context.theme.resolveAttribute(android.R.attr.actionBarItemBackground, this, true)
                resourceId
            })

            imageTintList = ColorStateList.valueOf(context.getColor(R.color.colorControlNormal))
        }

        findViewById<View>(R.id.search_edit_frame).updateLayoutParams<MarginLayoutParams> {
            leftMargin = 0
            rightMargin = 0
        }
    }
}