package ru.sudox.android.core.inject

import ru.sudox.android.core.CoreController
import ru.sudox.android.core.CoreFragment

/**
 * Интерфейс для внедрения зависимостей в ядро.
 *
 * Иногда используется просто для получения компонента, созданного в самом модуле приложения,
 * дабы избежать циклических зависимостей между модулями.
 */
interface CoreActivityComponent {
    fun inject(coreFragment: CoreFragment)
    fun inject(coreController: CoreController)
}