package com.sudox.android.ui.views.toolbar.expanded

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import com.sudox.android.common.helpers.drawAvatar
import com.sudox.android.common.helpers.drawCircleBitmap
import com.sudox.android.common.helpers.hideKeyboard
import com.sudox.android.data.database.model.Contact
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.ui.views.overlay.OverlappedRelativeLayout
import kotlinx.android.synthetic.main.founded_contact_add_layout.view.*

abstract class ExpandedView : RelativeLayout {

    internal var expanded: Boolean = false
    internal var turnBlackOverlay: Boolean = true
    var expandingCallback: ((Boolean) -> Unit)? = null

    // Анимация раскрытия
    private var animator = animate()
            .setStartDelay(0)
            .setDuration(300)

    init {
        // Чтобы не работали клики под нижними элементами (оверлей и т.п.).
        isFocusable = true
        isClickable = true
    }

    constructor(context: Context, turnBlackOverlay: Boolean) : super(context) {
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
                if (!expanded) {
                    focusedChild?.clearFocus()
                    hideKeyboard(context, this@ExpandedView)

                    // Clear all fields and etc ..
                    clear()
                }
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

        // Notify
        expandingCallback?.invoke(true)
    }

    fun hide() {
        animator.interpolator = AccelerateInterpolator()
        animator.translationY(-height.toFloat())
        expanded = false

        // Notify
        expandingCallback?.invoke(false)
    }
}