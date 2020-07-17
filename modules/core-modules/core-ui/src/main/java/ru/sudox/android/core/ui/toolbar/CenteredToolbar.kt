package ru.sudox.android.core.ui.toolbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.children
import com.google.android.material.appbar.MaterialToolbar
import ru.sudox.android.core.ui.R

/**
 * Доработанный Toolbar.
 *
 * В отличии от стандартного:
 * 1) Размещает заголовок по середине;
 * 2) Добавляет Tooltip на кнопку навигации.
 * 3) Выставляет отступы в зависимости от ситуации.
 */
class CenteredToolbar : MaterialToolbar {

    private var titleTextView: TextView? = null

    @VisibleForTesting
    var navigationButton: ImageButton? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setNavigationIcon(icon: Drawable?) {
        super.setNavigationIcon(icon)
        findNavigationButton { it.drawable == icon }
    }

    override fun setNavigationContentDescription(description: CharSequence?) {
        super.setNavigationContentDescription(description)
        findNavigationButton { it.contentDescription == description }

        if (navigationButton != null) {
            TooltipCompat.setTooltipText(navigationButton!!, description)
        }
    }

    override fun setTitle(title: CharSequence?) {
        if (titleTextView == null && title != null) {
            titleTextView = AppCompatTextView(context)
            titleTextView!!.layoutParams = generateDefaultLayoutParams().apply { gravity = Gravity.CENTER_HORIZONTAL }
            titleTextView!!.setTextAppearance(R.style.TextAppearance_Theme_Sudox_Toolbar)
            titleTextView!!.maxLines = 1
            titleTextView!!.text = title

            addView(titleTextView)
        }
    }

    private inline fun findNavigationButton(crossinline condition: (ImageButton) -> (Boolean)) {
        if (navigationButton == null) {
            navigationButton = children.find {
                it is ImageButton && condition(it)
            } as ImageButton
        }
    }
}