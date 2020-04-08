package ru.sudox.api.entries

import io.reactivex.rxjava3.core.ObservableEmitter

/**
 * Обьект системы обратных вызовов для запросов.
 *
 * @param subjectEmitter Эммитер Subject'а
 * @param dataClass Класс обьекта ответа
 */
data class ApiRequestCallback<T : Any>(
        val subjectEmitter: ObservableEmitter<T>,
        val dataClass: Class<T>
)