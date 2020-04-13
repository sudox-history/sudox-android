package ru.sudox.android.core.inject

import ru.sudox.android.core.CoreController

/**
 * Интерфейс для внедрения зависимостей в ядро.
 *
 * Иногда используется просто для получения компонента, созданного в самом модуле приложения,
 * дабы избежать циклических зависимостей между модулями.
 */
interface CoreActivityComponent {
    fun inject(coreController: CoreController)
}