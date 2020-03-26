package com.sudox.messenger.android.core

import com.sudox.design.appbar.vos.AppBarLayoutVO
import com.sudox.design.appbar.vos.AppBarVO
import com.sudox.messenger.android.core.inject.CoreComponent

interface CoreActivity {

    /**
     * Задает ViewObject'ы AppBar'у
     * Если передать null, то нужный элемент должен скрыться.
     *
     * @param appBarVO ViewObject, который необходимо задать AppBar'у
     * @param callback Кэллбэк для обработки кликов
     */
    fun setAppBarViewObject(appBarVO: AppBarVO?, callback: ((Int) -> (Unit))?)

    /**
     * Задает ViewObject'ы AppBarLayout'у
     * Если передать null, то нужный элемент должен скрыться.
     *
     * @param appBarLayoutVO ViewObject, который необходимо задать AppBarLayout'у
     */
    fun setAppBarLayoutViewObject(appBarLayoutVO: AppBarLayoutVO?)

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