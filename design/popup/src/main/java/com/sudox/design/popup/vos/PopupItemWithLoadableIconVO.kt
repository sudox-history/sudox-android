package com.sudox.design.popup.vos

import android.content.Context
import android.view.View
import com.sudox.design.popup.views.PopupItemView
import com.sudox.messenger.android.images.views.LoadableCircleImageView

/**
 * ViewObject с загружаемой иконкой для элемента Popup-окна
 * Информацию о предназначениях основных полей смотрите в классе PopupItemVO
 *
 * @param iconPhotoId ID фотографии, которую нужно загрузить
 */
@Suppress("EXPERIMENTAL_API_USAGE")
class PopupItemWithLoadableIconVO(
        override var tag: Int,
        override val title: String,
        override var isActive: Boolean,
        var iconPhotoId: Long
) : PopupItemVO<LoadableCircleImageView> {

    override fun getIconView(context: Context): LoadableCircleImageView? {
        return LoadableCircleImageView(context)
    }

    override fun <T : View> configureIconView(item: PopupItemView, view: T) {
        if (view is LoadableCircleImageView) {
            view.loadImage(iconPhotoId)
        }
    }

    override fun <T : View> detachIconView(view: T) {
    }
}