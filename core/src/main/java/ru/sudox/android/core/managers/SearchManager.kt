package ru.sudox.android.core.managers

import android.os.Bundle
import android.view.View

interface SearchManager {

    /**
     * Переключает поиск.
     * Меняет VO AppBar'а
     *
     * @param toggle Включить поиск?
     * @param editTextId ID EditText'а (для восстановления состояния)
     * @param callback Функция для обратного вызова
     * @param searchCallback Функция для обратного вызова поиска
     */
    fun toggleSearch(toggle: Boolean,
                     editTextId: Int = View.NO_ID,
                     callback: ((Int) -> Unit)? = null,
                     searchCallback: ((String) -> (Unit))? = null)

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
     * @param searchCallback Функция для обратного вызова поиска
     */
    fun restoreSearchState(bundle: Bundle?, callback: ((Int) -> Unit)? = null, searchCallback: ((String) -> (Unit))? = null)
}