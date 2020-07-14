package ru.sudox.android.core.ui.toolbar.helpers

import android.graphics.drawable.TransitionDrawable
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import ru.sudox.android.core.ui.scrollview.RestorableScrollView

private const val BACKGROUND_CHANGE_TRANSITION_DURATION = 300

/**
 * Анимирует показ фона AppBarLayout'а при скролле связанного с ним ScrollView.
 *
 * @param view ScrollView, который должен быть связан с AppBarLayout.
 */
fun AppBarLayout.setupWithScrollView(view: RestorableScrollView) {
    val background = background as TransitionDrawable
    var shown = false

    view.restoreStateEventCallback = {
        if (view.canScrollVertically(-1)) {
            background.startTransition(0)
            shown = true
        }
    }

    view.setOnScrollChangeListener { _, _, _, _, _ ->
        val canScrollToTop = view.canScrollVertically(-1)

        if (canScrollToTop && !shown) {
            background.startTransition(BACKGROUND_CHANGE_TRANSITION_DURATION)
            shown = true
        } else if (!canScrollToTop && shown) {
            background.reverseTransition(BACKGROUND_CHANGE_TRANSITION_DURATION)
            shown = false
        }
    }
}

/**
 * Анимирует показ фона AppBarLayout'а при скролле связанного с ним RecyclerView.
 *
 * @param view RecyclerView, который должен быть связан с AppBarLayout.
 */
fun AppBarLayout.setupWithRecyclerView(view: RecyclerView) {
    val background = background as TransitionDrawable
    var shown = false

    view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val canScrollToTop = view.canScrollVertically(-1)

            if (canScrollToTop && !shown) {
                background.startTransition(BACKGROUND_CHANGE_TRANSITION_DURATION)
                shown = true
            } else if (!canScrollToTop && shown) {
                background.reverseTransition(BACKGROUND_CHANGE_TRANSITION_DURATION)
                shown = false
            }
        }
    })
}