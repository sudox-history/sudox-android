package com.sudox.messenger.android.media.vos.impls

import android.content.Context
import android.view.View
import com.sudox.messenger.android.media.images.views.GlideImageView
import com.sudox.messenger.android.media.vos.MediaAttachmentType
import com.sudox.messenger.android.media.vos.MediaAttachmentVO

/**
 * ViewObject вложения в виде картинки.
 */
class ImageAttachmentVO(
        override var id: Long
) : MediaAttachmentVO {

    override var type = MediaAttachmentType.IMAGE
    override var height = 50
    override var width = 50
    override var order = 0

    override fun getView(context: Context): View {
        return GlideImageView(context)
    }

    override fun bindView(view: View) {
        if (view is GlideImageView) {
            view.loadImage(id)
        }
    }

    override fun unbindView(view: View) {
        if (view is GlideImageView) {
            view.cancelLoading()
        }
    }
}