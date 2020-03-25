package com.sudox.messenger.android.core

import com.sudox.design.appbar.AppBarVO
import com.sudox.messenger.android.core.inject.CoreComponent

interface CoreActivity {

    /**
     * Задает ViewObject'ы AppBar'у
     * Если передать null, то AppBar должен скрыться.
     *
     * @param vo ViewObject, который необходимо задать AppBar'у
     */
    fun setAppBarViewObject(vo: AppBarVO?)

    /**
     * Возвращает основной компонент ядра.
     * Вызывается при создании Fragment'ов
     *
     * @return Компонент ядра с заданными зависимостями.
     */

    fun getCoreComponent(): CoreComponent

    /**
     * Выдает обьект основного класса приложения.
     *
     * @return Обьект основного класса (Application)
     */
    @Deprecated(message = "Будет удален в будущем!")
    fun getLoader(): CoreLoader
}