package com.sudox.design.viewlist.vos

import android.content.Context
import android.graphics.drawable.Drawable

/**
 * ViewObject для шапки.
 */
interface ViewListHeaderVO {

    val isItemsHidden: Boolean
    val selectedToggleIndex: Int
    val selectedFunctionButtonToggleIndex: Int

    /**
     * Возвращает опции переключателя
     *
     * @param context Контекст приложения/активности
     * @return Пары опций тег-пара(название пункта, иконка пункта)
     */
    fun getToggleOptions(context: Context): Array<Pair<Int, Pair<String, Drawable?>>>

    /**
     * Возвращает конфигурацию функциональной кнопки
     *
     * @param context Контекст приложения/активности
     * @return Пара тег-иконка.
     */
    fun getFunctionButton(context: Context): Pair<Int, Drawable>?

    /**
     * Возвращает опции функциональной кнопки (если они есть)
     *
     * @param context Контекст приложения/активности
     * @return Пары тег-(текст, иконка)
     */
    fun getFunctionButtonToggleOptions(context: Context): Array<Pair<Int, Pair<String, Drawable>>>?

    /**
     * Определяет возможность скрытия элементов после шапки
     *
     * @return Можно ли скрыть предметы после себя?
     */
    fun canHideItems(): Boolean
}