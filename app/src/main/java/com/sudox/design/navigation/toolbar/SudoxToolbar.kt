package com.sudox.design.navigation.toolbar

import android.content.Context
import android.graphics.Color
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.sudox.android.R
import com.sudox.design.helpers.FontsHelper.Companion.SANS_SERIF_LIGHT

class SudoxToolbar : Toolbar {

    private var titleTextView: TextView? = null
    private var actionMenuView: ActionMenuView? = null
    private var navigationButtonView: ImageButton? = null
    private var featureTextButton: TextView? = null
    private var featureButtonText: String? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        readAttrs(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        readAttrs(attrs)
        init()
    }

    private fun init() {
        resetInsets()

        // Configuring toolbar
        configureMenu()
        configureNavigationButton()
        configureBasic()
    }

    private fun readAttrs(attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SudoxToolbar)

        // Read params
        featureButtonText = array.getString(R.styleable.SudoxToolbar_featureButtonText)

        // Recycle array & clean memory
        array.recycle()
    }

    private fun configureNavigationButton() {
        navigationButtonView = Toolbar::class.java
                .getDeclaredField("mNavButtonView")
                .apply { isAccessible = true }
                .get(this) as ImageButton

        if (navigationButtonView != null) {
            navigationButtonView?.setImageResource(R.drawable.ic_arrow_back)
        }

        if (featureButtonText != null) {
            featureTextButton = TextView(context)
            featureTextButton!!.text = featureButtonText
            featureTextButton!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
            featureTextButton!!.setTextColor(Color.WHITE)
            featureTextButton!!.gravity = Gravity.CENTER
            featureTextButton!!.layoutParams = generateDefaultLayoutParams().apply {
                gravity = GravityCompat.END or (getButtonGravity() and Gravity.VERTICAL_GRAVITY_MASK);
            }

            addSystemView(featureTextButton!!, false)
        }
    }

    private fun addSystemView(view: View, allowHide: Boolean) {
        Toolbar::class.java
                .getDeclaredMethod("addSystemView", View::class.java, Boolean::class.java)
                .apply { isAccessible = true }
                .invoke(this, view, allowHide)
    }

    private fun getButtonGravity(): Int {
        return Toolbar::class.java
                .getDeclaredField("mButtonGravity")
                .apply { isAccessible = true }
                .get(this) as Int
    }

    private fun configureBasic() {
        setBackgroundResource(R.color.colorPrimary)

        // Remove shadow
        elevation = 0F
    }

    private fun configureMenu() {
        if (featureButtonText == null) return

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

    fun setFeatureButtonOnClickListener(listener: View.OnClickListener) {
        if (featureTextButton == null) return

        // Bind listener
        featureTextButton!!.isClickable = true
        featureTextButton!!.isFocusable = true
        featureTextButton!!.setOnClickListener(listener)
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

        if (featureTextButton != null) {
            val shouldLayout = Toolbar::class.java
                    .getDeclaredMethod("shouldLayout", View::class.java)
                    .apply { isAccessible = true }
                    .invoke(this, featureTextButton) as Boolean

            if (shouldLayout) {
                val maxHeight = Toolbar::class.java
                        .getDeclaredField("mMaxButtonHeight")
                        .apply { isAccessible = true }
                        .get(this)

                Toolbar::class.java
                        .getDeclaredMethod("measureChildConstrained", View::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java,  Int::class.java)
                        .apply { isAccessible = true }
                        .invoke(this, featureTextButton, widthMeasureSpec, 0, heightMeasureSpec, 0, maxHeight)
            }
        }

        // Set new size
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        actionMenuView?.layoutParams = (actionMenuView?.layoutParams as Toolbar.LayoutParams)
                .apply { gravity = Gravity.CENTER_VERTICAL }

        navigationButtonView?.layoutParams = (navigationButtonView?.layoutParams as Toolbar.LayoutParams)
                .apply { gravity = Gravity.CENTER_VERTICAL }

        val shouldLayout = Toolbar::class.java
                .getDeclaredMethod("shouldLayout", View::class.java)
                .apply { isAccessible = true }
                .invoke(this, featureTextButton) as Boolean

        if (shouldLayout) {
            val minHeight = ViewCompat.getMinimumHeight(this)
            val alignmentHeight = if (minHeight >= 0) Math.min(minHeight, b - t) else 0

            val collapsingMargins = Toolbar::class.java
                    .getDeclaredField("mTempMargins")
                    .apply { isAccessible = true }
                    .get(this) as IntArray

            collapsingMargins[1] = 0
            collapsingMargins[0] = 0

            Toolbar::class.java
                    .getDeclaredMethod("layoutChildRight", View::class.java, Int::class.java, IntArray::class.java, Int::class.java)
                    .apply { isAccessible = true }
                    .invoke(this, featureTextButton, width - paddingRight, collapsingMargins, alignmentHeight)
        }
    }
}