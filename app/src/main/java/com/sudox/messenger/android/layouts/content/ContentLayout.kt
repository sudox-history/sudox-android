package com.sudox.messenger.android.layouts.content

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
import com.sudox.design.appbar.AppBar
import com.sudox.design.appbar.AppBarLayout
import com.sudox.messenger.android.R

class ContentLayout : CoordinatorLayout {

    val fragment = NavHostFragment.create(R.navigation.navigation_main)

    @Suppress("unused")
    val appBarLayout = AppBarLayout(context).apply {
        id = View.generateViewId()
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        appBar = AppBar(context)

        this@ContentLayout.addView(this)
    }

    val frameLayout = FragmentContainerView(context).apply {
        id = View.generateViewId()
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            behavior = com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior()
        }

        this@ContentLayout.addView(this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * Инициализирует FrameLayout, задает ему основной фрагмент навигации.
     *
     * @param fragmentManager Менеджер фрагментов
     */
    fun init(fragmentManager: FragmentManager) {
        fragmentManager
                .beginTransaction()
                .replace(frameLayout.id, fragment)
                .setPrimaryNavigationFragment(fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }
}