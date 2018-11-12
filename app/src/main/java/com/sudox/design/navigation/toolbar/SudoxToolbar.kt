package com.sudox.design.navigation.toolbar

import android.content.Context
import android.support.v4.view.MarginLayoutParamsCompat
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.sudox.android.R
import com.sudox.design.helpers.FontsHelper.Companion.SANS_SERIF_LIGHT

class SudoxToolbar : Toolbar {

    private var titleTextView: TextView? = null
    private var actionMenuView: ActionMenuView? = null
    private var navigationButtonView: ImageButton? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        resetInsets()

        // Configuring toolbar
        configureMenu()
        configureNavigationButton()
        configureBasic()
    }

    private fun configureNavigationButton() {
        navigationButtonView = Toolbar::class.java
                .getDeclaredField("mNavButtonView")
                .apply { isAccessible = true }
                .get(this) as ImageButton

        if (navigationButtonView != null) {
            navigationButtonView?.setImageResource(R.drawable.ic_arrow_back)
        }
    }

    private fun configureBasic() {
        setBackgroundResource(R.color.colorPrimary)

        // Remove shadow
        elevation = 0F
    }

    private fun configureMenu() {
        Toolbar::class.java
                .getDeclaredMethod("ensureMenuView")
                .apply { isAccessible = true }
                .invoke(this)

        // Cache menu for better performance
        actionMenuView = Toolbar::class.java
                .getDeclaredField("mMenuView")
                .apply { isAccessible = true }
                .get(this) as ActionMenuView
    }

    private fun configurePaddings() {
        val menuItemsCount = actionMenuView?.childCount ?: 0
        val initialStartPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25F, resources.displayMetrics)
        val initialEndPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25F, resources.displayMetrics)
        val startPadding = calculateStartPadding(initialStartPadding)
        val endPadding = calculateEndPadding(menuItemsCount, initialEndPadding.toInt())

        setPaddingRelative(startPadding, 0, endPadding, 0)
    }

    private fun calculateStartPadding(initialStartPadding: Float): Int {
        if (navigationButtonView != null) {
            return (initialStartPadding - navigationButtonView!!.drawable.intrinsicWidth).toInt()
        }

        return initialStartPadding.toInt()
    }

    private fun calculateEndPadding(menuItemsCount: Int, initialEndPadding: Int): Int {
        if (menuItemsCount > 0) {
            val lastItem = actionMenuView!!.getChildAt(menuItemsCount - 1)
            val lastItemEndPadding = if (lastItem.paddingEnd > 0) lastItem.paddingEnd else lastItem.paddingRight

            // Recalculate end padding
            return initialEndPadding - lastItemEndPadding
        }

        return initialEndPadding
    }

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)

        // Get title text view
        if (titleTextView == null) {
            titleTextView = Toolbar::class.java
                    .getDeclaredField("mTitleTextView")
                    .apply { isAccessible = true }
                    .get(this) as TextView
        }

        titleTextView?.includeFontPadding = false
        titleTextView?.typeface = SANS_SERIF_LIGHT
        titleTextView?.setPadding(0, 0, 0, 0)
        titleTextView?.setPaddingRelative(0, 0, 0, 0)
        titleTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19F)
    }

    private fun resetInsets() {
        setContentInsetsAbsolute(0, 0)
        setContentInsetsRelative(0, 0)

        Toolbar::class.java
                .getDeclaredField("mContentInsetStartWithNavigation")
                .apply { isAccessible = true }
                .set(this, 0)

        Toolbar::class.java
                .getDeclaredField("mContentInsetEndWithActions")
                .apply { isAccessible = true }
                .set(this, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Configure paddings
        configurePaddings()

        // Calculate new sizes
        val width = measuredWidthAndState
        val height = MeasureSpec.makeMeasureSpec(resources.getDimension(R.dimen.toolbar_height).toInt(), MeasureSpec.EXACTLY)

        // Set new size
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        // Align action menu to vertical center
        actionMenuView?.layoutParams = (actionMenuView?.layoutParams as Toolbar.LayoutParams)
                .apply { gravity = Gravity.CENTER_VERTICAL }

        navigationButtonView?.layoutParams = (navigationButtonView?.layoutParams as Toolbar.LayoutParams)
                .apply { gravity = Gravity.CENTER_VERTICAL }
    }
}