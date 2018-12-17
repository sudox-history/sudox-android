package com.sudox.design.navigation.toolbar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.sudox.android.ApplicationLoader
import com.sudox.android.R
import com.sudox.design.helpers.FontsHelper.Companion.SANS_SERIF_LIGHT
import com.sudox.protocol.ProtocolClient
import com.sudox.protocol.models.enums.ConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SudoxToolbar : Toolbar {

    private var titleTextView: TextView? = null
    private var actionMenuView: ActionMenuView? = null
    private var navigationButtonView: ImageButton? = null
    private var featureTextButton: TextView? = null
    private var featureButtonText: String? = null
    private var normalTitleText: String? = null
    private var connectionStateSubscription: ReceiveChannel<ConnectionState>? = null

    @Inject
    @JvmField
    var protocolClient: ProtocolClient? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (attrs != null) readAttrs(attrs)

        // Initialization
        resetInsets()

        // Configuring toolbar
        configureBasic()
        configureNavigationButton()
        configureMenu()
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
                .get(this) as? ImageButton

        if (navigationButtonView != null) {
            navigationButtonView?.setImageResource(R.drawable.ic_arrow_back)
        }

        if (featureButtonText != null) {
            featureTextButton = TextView(context)
            featureTextButton!!.text = featureButtonText
            featureTextButton!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
            featureTextButton!!.setTextColor(Color.WHITE)
            featureTextButton!!.gravity = Gravity.END
            featureTextButton!!.layoutParams = generateDefaultLayoutParams().apply {
                gravity = GravityCompat.END or Gravity.CENTER_VERTICAL;
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
        if (featureButtonText != null) return

        // Initialize the menu view
        Toolbar::class.java
                .getDeclaredMethod("ensureMenuView")
                .apply { isAccessible = true }
                .invoke(this)

        // Reflection is the single way to get & change mMenuView field
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
            val lastItem = actionMenuView!!.getChildAt(menuItemsCount - 1) as ActionMenuItemView
            val lastItemEndPadding = if (lastItem.paddingEnd > 0) lastItem.paddingEnd else lastItem.paddingRight
            val iconWidth = (ActionMenuItemView::class.java
                    .getDeclaredField("mIcon")
                    .apply { isAccessible = true }
                    .get(lastItem) as Drawable).intrinsicWidth

            // Recalculate end padding
            return initialEndPadding - lastItemEndPadding - (iconWidth / 2)
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
        normalTitleText = title.toString()

        listenConnectionState()
    }

    private fun listenConnectionState() {
        if (connectionStateSubscription != null) return
        if (protocolClient == null) ApplicationLoader.component.inject(this@SudoxToolbar)
        if (!protocolClient!!.isValid()) titleTextView?.setText(resources.getString(R.string.wait_for_connect))

        // Listen ...
        GlobalScope.launch(Dispatchers.IO) {
            connectionStateSubscription = protocolClient!!
                    .connectionStateChannel
                    .openSubscription()

            for (state in connectionStateSubscription!!) {
                if (state == ConnectionState.CONNECTION_CLOSED) {
                    GlobalScope.launch(Dispatchers.Main) { titleTextView!!.setText(resources.getString(R.string.wait_for_connect)) }
                } else if (state == ConnectionState.HANDSHAKE_SUCCEED) {
                    GlobalScope.launch(Dispatchers.Main) { titleTextView!!.setText(normalTitleText) }
                }
            }
        }
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

        // Calculate new sizes
        val width = measuredWidthAndState
        val height = MeasureSpec.makeMeasureSpec(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56F, resources.displayMetrics).toInt(),
                MeasureSpec.EXACTLY)

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
                        .getDeclaredMethod("measureChildConstrained", View::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java)
                        .apply { isAccessible = true }
                        .invoke(this, featureTextButton, widthMeasureSpec, 0, heightMeasureSpec, 0, maxHeight)
            }
        }

        // Set new size
        setMeasuredDimension(width, height)

        // Configure paddings
        configurePaddings()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        if (actionMenuView != null
                && (actionMenuView!!.layoutParams as Toolbar.LayoutParams).gravity != Gravity.CENTER_VERTICAL) {

            actionMenuView?.layoutParams = (actionMenuView?.layoutParams as Toolbar.LayoutParams)
                    .apply { gravity = Gravity.CENTER_VERTICAL }
        }

        if (navigationButtonView != null
                && (navigationButtonView!!.layoutParams as Toolbar.LayoutParams).gravity != Gravity.CENTER_VERTICAL) {

            navigationButtonView?.layoutParams = (navigationButtonView?.layoutParams as Toolbar.LayoutParams)
                    .apply { gravity = Gravity.CENTER_VERTICAL }
        }

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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        // Unsubscribe!
        connectionStateSubscription?.cancel()
    }
}