package com.sudox.design.viewlist.vos

import android.content.Context
import android.graphics.drawable.Drawable
import com.sudox.design.popup.vos.PopupItemVO

/**
 * ViewObject для шапки.
 */
interface ViewListHeaderVO {

    var type: Int
    var isItemsHidden: Boolean
    var isContentLoading: Boolean
    var isInClearLoading: Boolean
    var selectedToggleTag: Int
    var selectedFunctionButtonToggleTags: IntArray?

    /**
     * Проверяет возможность отображения элемента загрузчика
     */
    fun canShowLoader(): Boolean {
        return !isContentLoading && (!canHideItems() || !isItemsHidden)
    }

    /**
     * Проверяет возможность скрытия элемента загрузчика
     */
    fun canHideLoader(): Boolean {
        return isContentLoading && (!canHideItems() || !isItemsHidden)
    }

    /**
     * Проверяет отображение загрузчика
     */
    fun isLoaderShowing(): Boolean {
        return isContentLoading && ((canHideItems() && !isItemsHidden) || (!canHideItems()))
    }

    /**
     * Выбирает опцию функциональной кнопки и сопоставляет её опциональной кнопки переключателя
     *
     * @param functionalToggleTag Тег опции функциональной кнопки
     */
    fun selectFunctionalToggleTag(functionalToggleTag: Int) {
        selectedFunctionButtonToggleTags!![selectedToggleTag] = functionalToggleTag
    }

    /**
     * Возвращает опцию функциональной кнопки, сопоставленную опции кнопки переключателя
     *
     * @param toggleTag Опция кнопки переключателя (по-умолчанию выбирается выбранная)
     * @return Опция функциональной кнопки
     */
    fun getSelectedFunctionalToggleTag(toggleTag: Int = selectedToggleTag): Int {
        return selectedFunctionButtonToggleTags!![toggleTag]
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