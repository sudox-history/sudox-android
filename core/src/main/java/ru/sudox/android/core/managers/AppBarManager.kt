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
     * Вызывается при создании Activity
     */
    fun onStart()

    /**
     * Вызывается при остановке Activity
     */
    fun onStop()
}