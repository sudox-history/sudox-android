package com.sudox.messenger.android.time

import android.content.Context
import androidx.core.util.Pools
import java.util.Calendar

internal var calendarsPool = Pools.SimplePool<Calendar>(4)

/**
 * Выдает сокращенное название месяца
 *
 * @param context Контекст приложения/активности
 * @param number Номер месяца
 * @return Скоращенное название месяца
 */
fun getShortMonthName(context: Context, number: Int): String? {
    return when (number) {
        1 -> context.getString(R.string.january_short)
        2 -> context.getString(R.string.february_short)
        3 -> context.getString(R.string.march_short)
        4 -> context.getString(R.string.april_short)
        5 -> context.getString(R.string.may_short)
        6 -> context.getString(R.string.june_short)
        7 -> context.getString(R.string.july_short)
        8 -> context.getString(R.string.august_short)
        9 -> context.getString(R.string.september_short)
        10 -> context.getString(R.string.october_short)
        11 -> context.getString(R.string.november_short)
        12 -> context.getString(R.string.december_short)
        else -> null
    }
}

/**
 * Выдает полное название месяца
 *
 * @param context Контекст приложения/активности
 * @param number Номер месяца
 * @return Полное название месяца
 */
fun getFullMonthName(context: Context, number: Int): String? {
    return when (number) {
        1 -> context.getString(R.string.january)
        2 -> context.getString(R.string.february)
        3 -> context.getString(R.string.march)
        4 -> context.getString(R.string.april)
        5 -> context.getString(R.string.may)
        6 -> context.getString(R.string.june)
        7 -> context.getString(R.string.july)
        8 -> context.getString(R.string.august)
        9 -> context.getString(R.string.september)
        10 -> context.getString(R.string.october)
        11 -> context.getString(R.string.november)
        12 -> context.getString(R.string.december)
        else -> null
    }
}

/**
 * Выдает сокращенное название дня недели
 *
 * @param context Контекст приложения/активности
 * @param number Номер дня недели
 * @return Сокращенное название дня недели
 */
fun getShortNameOfDayOfWeek(context: Context, number: Int): String? {
    if (number < 1 || number > 7) {
        return null
    }

    return when (number) {
        1 -> context.getString(R.string.sunday_short)
        2 -> context.getString(R.string.monday_short)
        3 -> context.getString(R.string.tuesday_short)
        4 -> context.getString(R.string.wednesday_short)
        5 -> context.getString(R.string.thursday_short)
        6 -> context.getString(R.string.friday_short)
        7 -> context.getString(R.string.saturday_short)
        else -> null
    }
}

/**
 * Выдает полное название дня недели
 *
 * @param context Контекст приложения/активности
 * @param number Номер дня недели
 * @return Полное название дня недели
 */
fun getFullNameOfDayOfWeek(context: Context, number: Int): String? {
    if (number < 1 || number > 7) {
        return null
    }

    return when (number) {
        1 -> context.getString(R.string.sunday)
        2 -> context.getString(R.string.monday)
        3 -> context.getString(R.string.tuesday)
        4 -> context.getString(R.string.wednesday)
        5 -> context.getString(R.string.thursday)
        6 -> context.getString(R.string.friday)
        7 -> context.getString(R.string.saturday)
        else -> null
    }
}