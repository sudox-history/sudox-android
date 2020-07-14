package ru.sudox.android.core.ui.popup

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable

/**
 * RippleDrawable с отключенным скрытием по запросу от View.
 */
class CustomPopupMenuRippleDrawable(
    color: ColorStateList,
    content: Drawable?,
    mask: Drawable?
) : RippleDrawable(color, content, mask) {

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean =
        !(!visible && !restart) && super.setVisible(visible, restart)
}