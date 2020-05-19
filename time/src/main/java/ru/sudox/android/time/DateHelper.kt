package ru.sudox.android.time

import android.content.Context
import androidx.core.util.Pools
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Month
import java.util.Calendar

internal var calendarsPool = Pools.SimplePool<Calendar>(4)

/**
 * Выдает сокращенное название месяца
 *
 * @param context Контекст приложения/активности
 * @param month Месяц, имя которого нужно получить
 * @return Скоращенное название месяца
 */
fun getShortNameOfMonth(context: Context, month: Month): String {
    return when (month) {
        Month.JANUARY -> context.getString(R.string.january_short)
        Month.FEBRUARY -> context.getString(R.string.february_short)
        Month.MARCH -> context.getString(R.string.march_short)
        Month.APRIL -> context.getString(R.string.april_short)
        Month.MAY -> context.getString(R.string.may_short)
        Month.JUNE -> context.getString(R.string.june_short)
        Month.JULY -> context.getString(R.string.july_short)
        Month.AUGUST -> context.getString(R.string.august_short)
        Month.SEPTEMBER -> context.getString(R.string.september_short)
        Month.OCTOBER -> context.getString(R.string.october_short)
        Month.NOVEMBER -> context.getString(R.string.november_short)
        Month.DECEMBER -> context.getString(R.string.december_short)
    }
}

/**
 * Выдает полное название месяца
 *
 * @param context Контекст приложения/активности
 * @param month Месяц, имя которого нужно получить
 * @return Полное название месяца
 */
fun getFullNameOfMonth(context: Context, month: Month): String {
    return when (month) {
        Month.JANUARY -> context.getString(R.string.january)
        Month.FEBRUARY -> context.getString(R.string.february)
        Month.MARCH -> context.getString(R.string.march)
        Month.APRIL -> context.getString(R.string.april)
        Month.MAY -> context.getString(R.string.may)
        Month.JUNE -> context.getString(R.string.june)
        Month.JULY -> context.getString(R.string.july)
        Month.AUGUST -> context.getString(R.string.august)
        Month.SEPTEMBER -> context.getString(R.string.september)
        Month.OCTOBER -> context.getString(R.string.october)
        Month.NOVEMBER -> context.getString(R.string.november)
        Month.DECEMBER -> context.getString(R.string.december)
    }
}

/**
 * Выдает сокращенное название дня недели
 *
 * @param context Контекст приложения/активности
 * @param dayOfWeek День недели, имя которого нужно получить
 * @return Сокращенное название дня недели
 */
fun getShortNameOfDayOfWeek(context: Context, dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.SATURDAY -> context.getString(R.string.saturday_short)
        DayOfWeek.MONDAY -> context.getString(R.string.monday_short)
        DayOfWeek.TUESDAY -> context.getString(R.string.tuesday_short)
        DayOfWeek.WEDNESDAY -> context.getString(R.string.wednesday_short)
        DayOfWeek.THURSDAY -> context.getString(R.string.thursday_short)
        DayOfWeek.FRIDAY -> context.getString(R.string.friday_short)
        DayOfWeek.SUNDAY -> context.getString(R.string.sunday_short)
    }
}

/**
 * Выдает полное название дня недели
 *
 * @param context Контекст приложения/активности
 * @param dayOfWeek День недели, имя которого нужно получить
 * @return Полное название дня недели
 */
fun getFullNameOfDayOfWeek(context: Context, dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.SATURDAY -> context.getString(R.string.saturday)
        DayOfWeek.MONDAY -> context.getString(R.string.monday)
        DayOfWeek.TUESDAY -> context.getString(R.string.tuesday)
        DayOfWeek.WEDNESDAY -> context.getString(R.string.wednesday)
        DayOfWeek.THURSDAY -> context.getString(R.string.thursday)
        DayOfWeek.FRIDAY -> context.getString(R.string.friday)
        DayOfWeek.SUNDAY -> context.getString(R.string.sunday)
    }
}

/**
 * Выдает сокращенное название месяца
 *
 * @param context Контекст приложения/активности
 * @param number Номер месяца
 * @return Скоращенное название месяца
 */
@Deprecated(message = "replaced")
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
@Deprecated(message = "replaced")
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
@Deprecated(message = "replaced")
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
@Deprecated(message = "replaced")
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