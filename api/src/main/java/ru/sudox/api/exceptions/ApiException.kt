package ru.sudox.api.exceptions

/**
 * Исключение API.
 * Вызывается, когда метод API возвращает ошибку
 *
 * @param code Код ошибки.
 */
class ApiException(
        val code: Int
) : Exception()