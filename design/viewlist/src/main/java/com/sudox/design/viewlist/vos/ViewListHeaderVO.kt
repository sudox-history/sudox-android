package com.sudox.design.viewlist.vos

import android.content.Context
import android.graphics.drawable.Drawable
import com.sudox.design.popup.vos.PopupItemVO

/**
 * ViewObject для шапки.
 */
interface ViewListHeaderVO {

    var isItemsHidden: Boolean
    var selectedToggleIndex: Int
    var selectedFunctionButtonToggleIndex: Int

    /**
     * Возвращает опции переключателя
     *
     * @param context Контекст приложения/активности
     * @return ViewObject'ы элементов Popup-окна
     */
    fun getToggleOptions(context: Context): List<PopupItemVO<*>>

    /**
     * Возвращает иконку функциональной кнопки
     *
     * @param context Контекст приложения/активности
     * @return Иконка функциональной кнопки
     */
    fun getFunctionButtonIcon(context: Context): Drawable?

    /**
     * Возвращает опции функциональной кнопки (если они есть)
     *
     * @param context Контекст приложения/активности
     * @return ViewObject'ы элементов Popup-окна
     */
    fun getFunctionButtonToggleOptions(context: Context): List<PopupItemVO<*>>?

    /**
     * Определяет возможность скрытия элементов после шапки
     *
     * @return Можно ли скрыть предметы после себя?
     */
    fun canHideItems(): Boolean
}