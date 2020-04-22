package ru.sudox.design.bottomnavigationview

import android.content.Context
import android.util.AttributeSet
import androidx.transition.TransitionSet
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class TransitionlessBottomNavigationView : BottomNavigationView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        // Исправление бага с моргающим заголовком
        val menu = BottomNavigationView::class.java
                .getDeclaredField("menuView")
                .apply { isAccessible = true }
                .get(this) as BottomNavigationMenuView

        (BottomNavigationMenuView::class.java
                .getDeclaredField("set")
                .apply { isAccessible = true }
                .get(menu) as TransitionSet)
                .let {
                    it.removeTransition(it.getTransitionAt(0)!!)
                    it.duration = 0L
                }
    }
}