package com.sudox.android.ui.views.overlay

import android.content.Context
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout
import com.sudox.android.ui.views.NavigationBar

class OverlappedRelativeLayout : RelativeLayout {

    // Оверлей
    private lateinit var blackOverlayView: BlackOverlayView
    private var initialized: Boolean = false

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context) {
        blackOverlayView = BlackOverlayView(context).apply {
            layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }

        addView(blackOverlayView)
        bringChildToFront(blackOverlayView)
    }

    fun toggleOverlay(toggle: Boolean) = blackOverlayView.toggle(toggle)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        // Выносим Toolbar на передний план после оверлея
        if (!initialized) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)

                // Нужно задать View'шке высоту как у Toolbar'а
                if (child is Toolbar || child is NavigationBar) {
                    bringChildToFront(child)

                    // В Layout может быть только 1 Toolbar
                    break
                }
            }

            // Excluding next updates ...
            initialized = true
        }
    }
}