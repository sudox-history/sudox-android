package com.sudox.design.viewlist.vos

import android.content.Context
import android.graphics.drawable.Drawable
import com.sudox.design.popup.vos.PopupItemVO

/**
 * ViewObject для шапки.
 */
interface ViewListHeaderVO {

    var isItemsHidden: Boolean
    var isContentLoading: Boolean
    var selectedToggleTag: Int
    var selectedFunctionButtonToggleTags: HashMap<Int, Int>?

    /**
     * Выбирает опцию функциональной кнопки и сопоставляет её опциональной кнопки переключателя
     *
     * @param functionalToggleTag Тег опции функциональной кнопки
     */
    fun selectFunctionalToggleTag(functionalToggleTag: Int) {
        selectedFunctionButtonToggleTags?.remove(selectedToggleTag)
        selectedFunctionButtonToggleTags?.put(selectedToggleTag, functionalToggleTag)
    }

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
     * Определяет возможность сортировки элементов после шапки
     *
     * @return Можно ли сортировать предметы после себя?
     */
    fun canSortItems(): Boolean

    /**
     * Определяет возможность скрытия элементов после шапки
     *
     * @return Можно ли скрыть предметы после себя?
     */
    fun canHideItems(): Boolean
}