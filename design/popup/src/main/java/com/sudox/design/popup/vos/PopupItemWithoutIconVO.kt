package com.sudox.design.popup.vos

import android.content.Context
import android.view.View
import com.sudox.design.popup.views.PopupItemView

/**
 * ViewObject для элемента списка Popup-диалога без иконки
 */
class PopupItemWithoutIconVO(
        override val tag: Int,
        override val title: String,
        override val isActive: Boolean
) : PopupItemVO<View> {

    override fun getIconView(context: Context): View? {
        return null
    }

    override fun <T : View> configureIconView(item: PopupItemView, view: T) {
    }

    override fun <T : View> detachIconView(view: T) {
    }
}