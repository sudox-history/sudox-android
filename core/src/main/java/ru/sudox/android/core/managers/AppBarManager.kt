package ru.sudox.android.core.managers

import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO

interface AppBarManager {

    /**
     * Выставляет ViewObject AppBar'у
     * Если передать null, то скроет AppBar
     *
     * @param vo ViewObject AppBar'а.
     * @param callback Функция для обратного вызова.
     */
    fun setVO(vo: AppBarVO?, callback: ((Int) -> (Unit))?)

    /**
     * Выставляет ViewObject AppBarLayout'у
     * Если передать null, то скроет AppBarLayout
     *
     * @param vo ViewObject AppBarLayout'а.
     */
    fun setLayoutVO(vo: AppBarLayoutVO?)

    /**
     * Переключает загрузку.
     * Блокирует кнопки справа
     *
     * @param toggle Включить загрузку?
     */
    fun toggleLoading(toggle: Boolean)

    /**
     * Переключает поиск.
     * Меняет VO AppBar'а
     *
     * @param toggle Включить поиск?
     */
    fun toggleSearch(toggle: Boolean)

    /**
     * Определяет включенность поиска?
     *
     * @return True если поиск включен,
     * False если поиск выключен.
     */
    fun isSearchEnabled(): Boolean

    /**
     * Вызывается при создании Activity
     */
    fun onStart()

    /**
     * Вызывается при остановке Activity
     */
    fun onStop()
}