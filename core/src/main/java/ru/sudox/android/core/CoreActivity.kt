package ru.sudox.android.core

import ru.sudox.design.appbar.vos.AppBarLayoutVO
import ru.sudox.design.appbar.vos.AppBarVO
import ru.sudox.android.core.inject.CoreActivityComponent
import ru.sudox.android.core.inject.CoreLoaderComponent

/**
 * Интерфейс для связи CoreFragment и прочих классов с Activity.
 */
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
     * Возвращает основной компонет загрузчика.
     *
     * @return Компонент загрузчика с заданными зависимостями.
     */
    fun getLoaderComponent(): CoreLoaderComponent

    /**
     * Возвращает основной компонент активности.
     * Вызывается при создании Fragment'ов
     *
     * @return Компонент ядра с заданными зависимостями.
     */
    fun getActivityComponent(): CoreActivityComponent
}