package ru.sudox.api.entries

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для запроса API-метода
 *
 * @param methodName Название запрашиваемого метода
 * @param data Информация, подаваемая на метод
 */
@SudoxApiDTO
data class ApiRequest(
        val methodName: String,
        val data: Any
)