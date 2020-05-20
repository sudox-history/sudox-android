package ru.sudox.android.time

import android.content.Context
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit

/**
 * Переводит Timestamp в LocalDateTime
 *
 * @param timestamp Timestamp для перевода
 * @return LocalDateTime, получившийся в результате перевода
 */
fun dateTimeOf(timestamp: Long): LocalDateTime = Instant
        .ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

/**
 * Переводит unix-timestamp в строку с датой
 *
 * @param context Контекст приложения/активности
 * @param relativeTimestamp Время, относительно которого будет производится форматирование
 * @param timestamp Время, которое будет отформатировано
 * @return Строка с датой.
 */
fun timestampToDateString(context: Context, relativeTimestamp: Long = System.currentTimeMillis(), timestamp: Long): String {
    val relativeDateTime = dateTimeOf(relativeTimestamp)
    val requestDateTime = dateTimeOf(timestamp)
    val yearsDiff = ChronoUnit.YEARS.between(requestDateTime, relativeDateTime)
    val monthName = getFullNameOfMonth(context, requestDateTime.month)

    return if (yearsDiff == 0L) {
        context.getString(R.string.date_mask_without_year, requestDateTime.dayOfMonth, monthName)
    } else {
        context.getString(R.string.date_mask_with_year, requestDateTime.dayOfMonth, monthName, requestDateTime.year)
    }
}

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