package com.sudox.design.navigation.toolbar.expanded

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import com.sudox.design.helpers.hideKeyboard
import com.sudox.design.overlay.OverlappedRelativeLayout
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch

abstract class ExpandedView : RelativeLayout {

    internal var expanded: Boolean = false
    internal var turnBlackOverlay: Boolean = true
    var expandingCallback: ((Boolean) -> Unit)? = null

    // Анимация раскрытия
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
                if (turnBlackOverlay && parent is OverlappedRelativeLayout) {
                    (parent as OverlappedRelativeLayout).toggleOverlay(expanded)
                }
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (!expanded) {
                    focusedChild?.clearFocus()
                    hideKeyboard(context, this@ExpandedView)

                    // Clear all fields and etc ..
                    clear()
                }
            }
        })
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

    fun show() = GlobalScope.launch(Dispatchers.Main) {
        animator.interpolator = DecelerateInterpolator()
        animator.translationY(0F)
        expanded = true

        // Notify
        expandingCallback?.invoke(true)
    }

    fun hide() = GlobalScope.launch(Dispatchers.Main) {
        animator.interpolator = AccelerateInterpolator()
        animator.translationY(-height.toFloat())
        expanded = false

        // Notify
        expandingCallback?.invoke(false)
    }
}