package ru.sudox.android.core.ui.popup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ru.sudox.android.core.ui.R

internal val CHECKABLE_STATE_SET = intArrayOf(android.R.attr.state_checkable)

/**
 * View для элемента CustomPopupMenu.
 */
class CustomPopupMenuItemView : LinearLayout {

    private var isFinallyActive = false
    private var rippleDrawable: RippleDrawable
    private lateinit var iconView: ImageView
    private lateinit var titleView: TextView

    var item: MenuItem? = null
        set(value) {
            iconView.setImageDrawable(value!!.icon)
            contentDescription = value.title
            titleView.text = value.title

            setActive(value.isChecked)

            if (isFinallyActive != value.isCheckable) {
                isFinallyActive = value.isCheckable
                refreshDrawableState()
            }

            field = value
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val maskDrawable = GradientDrawable().apply { setColor(Color.WHITE) }
        val rippleColorStateList = context.getColorStateList(R.color.popup_item_ripple_color)

        rippleDrawable = CustomPopupMenuRippleDrawable(rippleColorStateList, null, maskDrawable)
        background = rippleDrawable
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        iconView = findViewById(R.id.popupItemIcon)
        titleView = findViewById(R.id.popupItemTitle)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val state = super.onCreateDrawableState(extraSpace + 1)

        if (isFinallyActive) {
            View.mergeDrawableStates(state, CHECKABLE_STATE_SET)
        }

        return state
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        rippleDrawable.setHotspot(event.x, event.y)
        return super.onTouchEvent(event)
    }

    /**
     * Активирует/дезактивирует пункт меню.
     * Меняет цвет компонентов в зависимости от состояния.
     *
     * @param active Активен ли пункт меню?
     */
    fun setActive(active: Boolean) {
        iconView.isActivated = active
        titleView.isActivated = active
    }
}