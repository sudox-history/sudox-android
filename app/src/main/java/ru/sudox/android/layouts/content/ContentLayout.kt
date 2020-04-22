package ru.sudox.android.layouts.content

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bluelinelabs.conductor.ChangeHandlerFrameLayout
import ru.sudox.design.appbar.AppBar
import ru.sudox.design.appbar.CustomAppBarLayout

/**
 * Layout, содержащий AppBarLayout и FrameLayout.
 * Отвечает за отображение тулбара и контента в приложении.
 */
class ContentLayout : CoordinatorLayout {

    @Suppress("unused")
    val appBarLayout = CustomAppBarLayout(context).apply {
        fitsSystemWindows = true
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        appBar = AppBar(context).apply { id = View.generateViewId() }

        this@ContentLayout.addView(this)
    }

    val frameLayout = ChangeHandlerFrameLayout(context).apply {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            behavior = com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior()
        }

        this@ContentLayout.addView(this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}