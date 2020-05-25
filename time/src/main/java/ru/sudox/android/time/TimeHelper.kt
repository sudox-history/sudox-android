package ru.sudox.android.time

import android.content.Context
import android.text.format.DateFormat
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.ChronoUnit
import ru.sudox.android.time.formatters.TimeFormatter

/**
 * Переводит DateTime в строку с временем.
 *
 * @param context Контекст приложения/активности
 * @param dateTime DateTime, который нужно перевести в строку
 * @param twelveHoursFormat Нужно ли работаь в 12-и часовом формате?
 * @return Строка с временем, которое было в timestamp.
 */
fun timestampToTimeString(
        context: Context,
        dateTime: LocalDateTime,
        twelveHoursFormat: Boolean = !DateFormat.is24HourFormat(context)
): String {
    val minute = addLeadingZeroToNumber(dateTime.minute)

    return if (twelveHoursFormat) {
        val amPm = context.getString(if (dateTime[ChronoField.AMPM_OF_DAY] == 1) {
            R.string.pm
        } else {
            R.string.am
        })

        context.getString(R.string.time_mask_with_am_pm, addLeadingZeroToNumber(dateTime[ChronoField.HOUR_OF_AMPM]), minute, amPm)
    } else {
        context.getString(R.string.time_mask_without_am_pm, addLeadingZeroToNumber(dateTime.hour), minute)
    }
}

/**
 * Переводит timestamp в строку нужного формата.
 *
 * @param context Контекст приложения/активности
 * @param relativeTimestamp Timestamp, относительно которого производится форматирование.
 * @param formatter Обьект, реализующий форматирование
 * @param timestamp Timestamp, который нужно отформатировать
 * @return Строка с отформатированным Timestamp.
 */
fun timestampToString(
        context: Context,
        relativeTimestamp: Long = System.currentTimeMillis(),
        useTwelveHoursFormat: Boolean = !DateFormat.is24HourFormat(context),
        formatter: TimeFormatter,
        timestamp: Long
): String {
    val relativeDateTime = dateTimeOf(relativeTimestamp)
    val requestDateTime = dateTimeOf(timestamp)
    val minutesDiff = ChronoUnit.MINUTES.between(requestDateTime, relativeDateTime)

    if (minutesDiff == 0L) {
        return formatter.onEventOccurredNow(context, requestDateTime, relativeDateTime, useTwelveHoursFormat)
    } else if (minutesDiff < 60L) {
        return formatter.onEventOccurredBetween1And60MinutesAgo(context, minutesDiff.toInt(), requestDateTime, relativeDateTime, useTwelveHoursFormat)
    }

    val hoursDiff = minutesDiff / 60

    if (hoursDiff in 1 .. 12) {
        return formatter.onEventOccurredBetween1And12HoursAgo(context, hoursDiff.toInt(), requestDateTime, relativeDateTime, useTwelveHoursFormat)
    } else if (hoursDiff in 13 until 24) {
        return formatter.onEventOccurredBetween12And24HoursAgo(context, hoursDiff.toInt(), requestDateTime, relativeDateTime, useTwelveHoursFormat)
    }

    val daysDiff = hoursDiff / 24L

    if (daysDiff == 1L) {
        return formatter.onEventOccurredYesterday(context, requestDateTime, relativeDateTime, useTwelveHoursFormat)
    } else if (daysDiff in 2L .. 7L) {
        return formatter.onEventOccurredBetween2And7DaysAgo(context, daysDiff.toInt(), requestDateTime, relativeDateTime, useTwelveHoursFormat)
    }

    val yearsDiff = ChronoUnit.YEARS.between(requestDateTime, relativeDateTime)

    if (yearsDiff == 0L) {
        return formatter.onEventOccurredInSameYear(context, requestDateTime, relativeDateTime, useTwelveHoursFormat)
    }

    return formatter.onEventOccurredInAnotherYear(context, yearsDiff.toInt(), requestDateTime, relativeDateTime, useTwelveHoursFormat)
}

internal fun addLeadingZeroToNumber(number: Int): String {
    if (number >= 10) {
        return number.toString()
    }

    return "0$number"
}