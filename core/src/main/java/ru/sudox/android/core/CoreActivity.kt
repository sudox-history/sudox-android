package ru.sudox.android.core

import ru.sudox.android.core.inject.CoreActivityComponent
import ru.sudox.android.core.inject.CoreLoaderComponent

/**
 * Интерфейс для связи CoreFragment и прочих классов с Activity.
 */
interface CoreActivity {

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