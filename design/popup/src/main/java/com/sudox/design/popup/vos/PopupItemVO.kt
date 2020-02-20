package com.sudox.design.popup.vos

import android.content.Context
import android.view.View
import com.sudox.design.popup.views.PopupItemView

/**
 * ViewObject для элемента списка Popup-диалога
 */
interface PopupItemVO<V : View> {

    val tag: Int
    val title: String
    val isActive: Boolean

    /**
     * Возвращает View, которую нужно отображать как иконку
     *
     * @param context Контекст приложения/активности
     * @return View для отображения.
     */
    fun getIconView(context: Context): V?

    /**
     * Конфигурирует View иконки.
     *
     * @param item View элемента списка
     * @param view View, который нужно сконфигурировать
     */
    fun <T : View> configureIconView(item: PopupItemView, view: T)

    /**
     * Останавливает все задачи внутри View
     *
     * @param view View, в которой нужно остановить загрузку
     */
    fun <T : View> detachIconView(view: T)
}