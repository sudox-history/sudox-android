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

    private var visible: Boolean = false
    private var animator = animate()
            .setStartDelay(0)
            .setDuration(300)

    constructor(context: Context) : super(context)
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
                if (parent is OverlappedRelativeLayout) (parent as OverlappedRelativeLayout).toggleOverlay(visible)
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
        if (!visible && changed) {
            translationY = -height.toFloat()
        }
    }

    /**
     * Общая логика для всех раскрывающихся View'шек
     **/
    fun toggle(toggle: Boolean) {
        if (toggle && !visible) {
            show()
        } else if (visible) {
            hide()
        }
    }

    fun toggle() {
        toggle(!visible)
    }

    fun show() {
        animator.interpolator = DecelerateInterpolator()
        animator.translationY(0F)
        visible = true
    }

    fun hide() {
        animator.interpolator = AccelerateInterpolator()
        animator.translationY(-height.toFloat())
        visible = false
    }
}