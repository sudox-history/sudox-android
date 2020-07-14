package ru.sudox.android.core.ui.toolbar

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.SearchView
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
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val searchButton = findViewById<View>(R.id.search_button)

        findViewById<View>(R.id.search_src_text).setPadding(0, 0, 0, 0)
        findViewById<View>(R.id.search_close_btn).apply {
            setPaddingRelative(searchButton.paddingStart, 0, searchButton.paddingEnd, 0)
            setBackgroundResource(with(TypedValue()) {
                context.theme.resolveAttribute(android.R.attr.actionBarItemBackground, this, true)
                resourceId
            })
        }

        findViewById<View>(R.id.search_edit_frame).updateLayoutParams<MarginLayoutParams> {
            leftMargin = 0
            rightMargin = 0
        }
    }
}