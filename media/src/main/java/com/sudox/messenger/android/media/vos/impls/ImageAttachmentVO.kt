package com.sudox.messenger.android.media.vos.impls

import android.content.Context
import android.view.View
import com.sudox.messenger.android.media.images.views.GlideImageView
import com.sudox.messenger.android.media.images.vos.ImageVO
import com.sudox.messenger.android.media.vos.MediaAttachmentType
import com.sudox.messenger.android.media.vos.MediaAttachmentVO

/**
 * ViewObject вложения в виде картинки.
 */
class ImageAttachmentVO(
        override var id: Long
) : MediaAttachmentVO, ImageVO {

    override var type = MediaAttachmentType.IMAGE
    override var height = 0
    override var width = 0
    override var order = 0

    override fun getView(context: Context): View {
        return GlideImageView(context)
    }

    override fun bindView(view: View) {
        if (view is GlideImageView) {
            view.vo = this
        }
    }

    override fun unbindView(view: View) {
        if (view is GlideImageView) {
            view.vo = null
        }
    }

    override fun getImageId(): Long {
        return id
    }
}