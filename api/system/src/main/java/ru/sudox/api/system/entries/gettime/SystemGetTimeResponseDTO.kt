package ru.sudox.api.system.entries.gettime

/**
 * Data Transfer Object для метода system.getTime()
 *
 * @param time Время на сервере по Гринвичу
 */
data class SystemGetTimeResponseDTO(val time: Long)