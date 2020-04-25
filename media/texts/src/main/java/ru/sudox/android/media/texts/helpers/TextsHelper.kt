package ru.sudox.android.media.texts.helpers

import android.content.Context
import ru.sudox.android.text.R

/**
 * Форматирует число, подставляя множительный суффикс вместо главного разряда.
 * Пример: 1230 - 1.2k, 1 000 000 - 1m
 *
 * @param context Контекст приложения/активности
 * @param number Форматируемое число
 */
fun formatNumber(context: Context, number: Int): String {
    if (number in 0 .. 999) {
        return number.toString()
    }

    val mask = if (number % 1000 == 0) {
        "%.0f"
    } else {
        "%.1f"
    }

    return if (number in 1000 .. 999_999) {
        String.format("$mask${context.getString(R.string.thousands_suffix)}", number / 1000F)
    } else if (number in 1_000_000 .. 999_999_999) {
        String.format("$mask${context.getString(R.string.millions_suffix)}", number / 1_000_000F)
    } else {
        String.format("$mask${context.getString(R.string.billions_suffix)}", number / 1_000_000_000F)
    }
}

/**
 * Генерирует строку из первых букв первых двух слов
 *
 * @param string Строка, из которой нужно получить новую
 * @return Сгенерированная строка
 */
fun getTwoFirstLetters(string: String): String {
    val builder = StringBuilder()
    val names = string.split(" ")

    if (names.isNotEmpty()) {
        builder.append(names[0][0])

        if (names.size >= 2) {
            builder.append(names[1][0])
        }
    }

    return builder.toString()
}