package ru.sudox.android.core

import ru.sudox.android.core.inject.CoreLoaderComponent

/**
 * Интерфейс для связи сервисов и т.п. компонентов
 * с контекстом приложения
 */
interface CoreLoader {

    /**
     * Возвращает Dagger-компонент загрузчика приложения
     * Используется для Dependency Injection
     *
     * @return Компонент загрузчика приложения
     */
    fun getComponent(): CoreLoaderComponent
}