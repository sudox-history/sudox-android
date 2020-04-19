package ru.sudox.android.core.managers

import android.os.Bundle
import android.view.View
import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO

interface AppBarManager {

    /**
     * Выставляет ViewObject AppBar'у
     * Если передать null, то скроет AppBar
     *
     * @param vo ViewObject AppBar'а.
     * @param callback Функция для обратного вызова.
     * @param force Игнорировать проверку
     */
    fun setVO(vo: AppBarVO?, callback: ((Int) -> (Unit))?, force: Boolean = false)

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
     * @param editTextId ID EditText'а (для восстановления состояния)
     * @param callback Функция для обратного вызова
     */
    fun toggleSearch(toggle: Boolean, editTextId: Int = View.NO_ID, callback: ((Int) -> Unit)? = null)

    /**
     * Определяет включенность поиска?
     *
     * @return True если поиск включен,
     * False если поиск выключен.
     */
    fun isSearchEnabled(): Boolean

    /**
     * Сохраняет состояние поиска.
     *
     * @param bundle Bundle, в который нужно сохранить состояние поиска
     */
    fun saveSearchState(bundle: Bundle)

    /**
     * Восстанавливает состояние поиска.
     *
     * @param bundle Bundle, из которого нужно восстановить состояние поиска.
     * @param callback Функция для обратного вызова
     */
    fun restoreSearchState(bundle: Bundle?, callback: ((Int) -> Unit)? = null)

    /**
     * Вызывается при создании Activity
     */
    fun onStart()

    /**
     * Вызывается при остановке Activity
     */
    fun onStop()
}