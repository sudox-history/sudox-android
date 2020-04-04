package com.sudox.messenger.android.core.inject

import com.sudox.messenger.android.core.CoreFragment

/**
 * Интерфейс для внедрения зависимостей в ядро.
 *
 * Иногда используется просто для получения компонента, созданного в самом модуле приложения,
 * дабы избежать циклических зависимостей между модулями.
 */
interface CoreActivityComponent {
    fun inject(coreFragment: CoreFragment)
}