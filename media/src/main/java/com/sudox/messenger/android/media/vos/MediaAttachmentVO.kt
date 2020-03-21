package com.sudox.messenger.android.media.vos

import android.content.Context
import android.view.View

/**
 * ViewObject вложения.
 *
 * @param id ID вложения
 * @param type Тип вложения
 * @param order Порядок размещения
 * @param height Высота вложения (если необходимо)
 * @param width Ширина вложения (если необходимо)
 */
interface MediaAttachmentVO {

    var id: Long
    var type: MediaAttachmentType
    var height: Int
    var width: Int
    var order: Int

    /**
     * Выдает View для отображения
     * Вызывается только если тип View сменился и требуется новая
     *
     * @param context Контекст активности/приложения
     */
    fun getView(context: Context): View

    /**
     * Забивает данные в предоставленный View
     *
     * @param view View в который нужно загрузить данные.
     */
    fun bindView(view: View)

    /**
     * Удаляет данные из старого View
     *
     * @param view View с которого нужно отгрузить данные
     */
    fun unbindView(view: View)
}