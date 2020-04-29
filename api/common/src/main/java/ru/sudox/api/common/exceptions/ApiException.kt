package ru.sudox.api.common.exceptions

/**
 * Исключение уровня API.
 * Вызывается, когда метод API возвращает ошибку
 *
 * @param methodName Название метода
 * @param code Код ошибки.
 */
class ApiException(
        val methodName: String,
        val code: Int
) : Exception() {

    override fun getLocalizedMessage(): String {
        return "Method $methodName returned error: $code"
    }
}