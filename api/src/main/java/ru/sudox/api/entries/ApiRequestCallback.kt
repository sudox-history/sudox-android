package ru.sudox.api.entries

import io.reactivex.rxjava3.core.SingleEmitter

/**
 * Обьект системы обратных вызовов для запросов.
 *
 * @param subjectEmitter Эммитер SingleSubject'а
 * @param dataClass Класс обьекта ответа
 */
data class ApiRequestCallback<T : Any>(
        val subjectEmitter: SingleEmitter<T>,
        val dataClass: Class<T>
)