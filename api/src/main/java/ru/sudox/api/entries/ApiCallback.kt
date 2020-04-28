package ru.sudox.api.entries

import io.reactivex.ObservableEmitter

/**
 * Обьект системы обратных вызовов.
 *
 * @param observableEmitter Эммитер Observable'а
 * @param dataClass Класс обьекта ответа
 */
data class ApiCallback<T : Any>(
        val observableEmitter: ObservableEmitter<T>,
        val dataClass: Class<T>
)