package ru.sudox.api.common.exceptions

/**
 * Исключение уровня API.
 * Вызывается, когда метод API возвращает ошибку
 *
 * @param code Код ошибки.
 */
class ApiException(
        val code: Int
) : Exception()