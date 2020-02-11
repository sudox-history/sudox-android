package com.sudox.design.popup.vos

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import com.sudox.design.imageview.ImageView
import com.sudox.design.popup.views.PopupItemView

/**
 * ViewObject для элемента списка Popup-диалога с иконкой в виде Drawable
 */
class PopupItemWithDrawableIconVO : PopupItemVO<ImageView> {

    private var iconDrawable: Drawable? = null
    private var iconDrawableRes = 0

    override var tag: Int = 0
    override var isActive: Boolean = false
    override val title: String

    /**
     * Создает ViewObject с уже созданным заранее Drawable
     * Информацию по другим полям смотрите в классе PopupItemVO
     *
     * @param iconDrawable Drawable иконки
     */
    constructor(tag: Int, title: String, iconDrawable: Drawable, isActive: Boolean) {
        this.tag = tag
        this.title = title
        this.isActive = isActive
        this.iconDrawable = iconDrawable
    }

    /**
     * Создает ViewObject с Drawable, который нужно достать из ресурсов
     * Информацию по другим полям смотрите в классе PopupItemVO
     *
     * @param iconDrawableRes ID Drawable с иконкой
     */
    constructor(tag: Int, title: String, iconDrawableRes: Int, isActive: Boolean) {
        this.tag = tag
        this.title = title
        this.isActive = isActive
        this.iconDrawableRes = iconDrawableRes
    }

    override fun getIconView(context: Context): ImageView? {
        return ImageView(context)
    }

    override fun <T : View> configureIconView(item: PopupItemView, view: T) {
        if (view is ImageView) {
            view.setDrawable(iconDrawable ?: view.context.getDrawable(iconDrawableRes), if (isActive) {
                item.activeTitleColor
            } else {
                item.inactiveTitleColor
            })
        }
    }

    override fun <T : View> detachIconView(view: T) {
    }
}