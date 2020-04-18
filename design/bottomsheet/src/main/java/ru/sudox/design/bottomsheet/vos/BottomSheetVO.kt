package ru.sudox.design.bottomsheet.vos

import android.content.Context
import android.view.View

/**
 * ViewObject для BottomSheetLayout
 */
interface BottomSheetVO {

    /**
     * Возвращает заголовок Layout'а
     *
     * @param context Контекст приложения/активности
     * @return Заголовок Layout'а (null если не нужен)
     */
    fun getTitle(context: Context): String?

    /**
     * Возвращает контент Layout'а
     *
     * @param context Контекст приложения/активности
     * @return Контент Layout'а (null если не нужен)
     */
    fun getContentView(context: Context): View?

    /**
     * Возвращает кнопки Layout'а
     *
     * @param context Контекст приложения/активности
     * @return View кнопок Layout'а (null если не нужен)
     */
    fun getButtonsViews(context: Context): Array<View>?
}