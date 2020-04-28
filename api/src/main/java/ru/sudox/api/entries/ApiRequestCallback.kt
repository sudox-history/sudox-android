package ru.sudox.api.entries

import io.reactivex.ObservableEmitter

/**
 * Обьект системы обратных вызовов для запросов.
 *
 * @param observableEmitter Эммитер Observable'а
 * @param dataClass Класс обьекта ответа
 */
data class ApiRequestCallback<T : Any>(
        val observableEmitter: ObservableEmitter<T>,
        val dataClass: Class<T>
)