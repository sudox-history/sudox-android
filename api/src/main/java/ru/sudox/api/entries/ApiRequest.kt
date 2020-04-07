package ru.sudox.api.entries

/**
 * Data Transfer Object для запроса API-метода
 *
 * @param methodName Название запрашиваемого метода
 * @param data Информация, подаваемая на метод
 */
data class ApiRequest(
        val methodName: String,
        val data: Any
)