package com.sudox.android.ui.views.overlay

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.RelativeLayout

class OverlappedRelativeLayout : RelativeLayout {

    // Оверлей
    private lateinit var blackOverlayView: BlackOverlayView

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
}