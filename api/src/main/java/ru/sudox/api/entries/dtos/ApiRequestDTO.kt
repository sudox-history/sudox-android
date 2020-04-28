package ru.sudox.api.entries.dtos

import ru.sudox.api.common.SudoxApiDTO

/**
 * Data Transfer Object для запроса API-метода
 *
 * @param methodName Название запрашиваемого метода
 * @param data Информация, подаваемая на метод
 */
@SudoxApiDTO
data class ApiRequestDTO(
        val methodName: String,
        val data: Any
)