package com.sudox.android.ui.views.toolbar.expanded

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import com.sudox.android.common.helpers.hideKeyboard
import com.sudox.android.ui.views.overlay.OverlappedRelativeLayout

abstract class ExpandedView : RelativeLayout {

    var expanded: Boolean = false
    var turnBlackOverlay: Boolean = true
    private var animator = animate()
            .setStartDelay(0)
            .setDuration(300)

    constructor(context: Context, turnBlackOverlay: Boolean) : super(context){
        this.turnBlackOverlay = turnBlackOverlay
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * В этом методе нужно удалить все данные, которые были во View
     **/
    abstract fun clear()

    init {
        animator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {
                if (turnBlackOverlay && parent is OverlappedRelativeLayout)
                    (parent as OverlappedRelativeLayout).toggleOverlay(expanded)
            }

            override fun onAnimationEnd(animation: Animator?) {
                focusedChild?.clearFocus()
                hideKeyboard(context, this@ExpandedView)

                // Clear all fields and etc ..
                clear()
            }
        })

        isFocusable = true
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        // Hide this view if bottom padding is negative
        if (!expanded && changed) {
            translationY = -height.toFloat()
        }
    }

    /**
     * Общая логика для всех раскрывающихся View'шек
     **/
    fun toggle(toggle: Boolean) {
        if (toggle && !expanded) {
            show()
        } else if (expanded) {
            hide()
        }
    }

    fun toggle() {
        toggle(!expanded)
    }

    fun show() {
        animator.interpolator = DecelerateInterpolator()
        animator.translationY(0F)
        expanded = true
    }

    fun hide() {
        animator.interpolator = AccelerateInterpolator()
        animator.translationY(-height.toFloat())
        expanded = false
    }
}