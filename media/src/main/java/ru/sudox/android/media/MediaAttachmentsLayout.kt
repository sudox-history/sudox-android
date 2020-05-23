package ru.sudox.android.media

import android.content.Context
import android.util.AttributeSet
import ru.sudox.android.media.images.GlideRequests
import ru.sudox.design.mityushkinlayout.MityushkinLayout
import ru.sudox.android.media.vos.MediaAttachmentVO

class MediaAttachmentsLayout : MityushkinLayout {

    var vos: ArrayList<MediaAttachmentVO>? = null
        private set

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * Устанавливает ViewObject'ы вложений в данную View
     *
     * @param vos ViewObject'ы, которые нужно использовать
     * @param glide Glide для загрузки изображений/GIF'ок
     */
    fun setVOs(vos: ArrayList<MediaAttachmentVO>?, glide: GlideRequests) {
        if (this@MediaAttachmentsLayout.vos != null) {
            for (i in 0 until childCount) {
                removeViewInLayout(getChildAt(0).apply {
                    this@MediaAttachmentsLayout.vos!![i].unbindView(this, glide)
                })
            }
        }

        if (vos != null) {
            val isLayoutDependsFromChildSize = adapter!!.getTemplate(vos.size)!!.dependsFromChildSize

            for (i in 0 until vos.size) {
                val vo = vos[i]
                val view = vo.getView(context)

                if (isLayoutDependsFromChildSize) {
                    addView(view, -1, LayoutParams(vo.width, vo.height))
                } else {
                    addView(view)
                }

                vo.bindView(view, glide)
            }
        }

        this.vos = vos
        requestLayout()
        invalidate()
    }
}