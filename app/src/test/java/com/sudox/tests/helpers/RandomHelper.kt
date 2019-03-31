package com.sudox.tests.helpers

import android.util.Base64
import java.security.SecureRandom

/**
 * Генерирует случайную последовательность байтов указанной длины и кодирует в Base64.
 *
 * @param length - количество байтов для кодирования.
 */
fun randomBase64String(length: Int): String {
    val bytes = ByteArray(length)

    // Генерируем рандомные байты
    SecureRandom().nextBytes(bytes)

    // Переводим в Base-64
    return Base64
            .encodeToString(bytes, Base64.NO_WRAP)
            .replace("\n", "")
}