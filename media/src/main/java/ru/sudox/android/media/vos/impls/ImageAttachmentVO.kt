package ru.sudox.android.media.vos.impls

import android.content.Context
import android.view.View
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.android.media.images.views.GlideImageView
import ru.sudox.android.media.vos.MediaAttachmentType
import ru.sudox.android.media.vos.MediaAttachmentVO

/**
 * ViewObject вложения в виде картинки.
 */
class ImageAttachmentVO(
        override var id: Long
) : MediaAttachmentVO {

    override var type = MediaAttachmentType.IMAGE
    override var height = 0
    override var width = 0
    override var order = 0

    override fun getView(context: Context): View {
        return GlideImageView(context)
    }

    override fun bindView(view: View, glide: GlideRequests) {
        if (view is GlideImageView) {
            view.loadImage(id, glide)
        }
    }

    override fun unbindView(view: View, glide: GlideRequests) {
        if (view is GlideImageView) {
            view.cancelLoading(glide)
        }
    }
}