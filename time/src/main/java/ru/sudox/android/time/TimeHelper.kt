package ru.sudox.android.time

import android.content.Context
import android.text.format.DateFormat
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.ChronoUnit
import ru.sudox.android.time.formatters.TimeFormatter
import java.util.Calendar
import java.util.Calendar.AM_PM
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.DAY_OF_WEEK
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.HOUR
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE
import java.util.Calendar.MONTH
import java.util.Calendar.SECOND
import java.util.Calendar.WEEK_OF_YEAR
import java.util.Calendar.YEAR
import java.util.Calendar.getInstance
import java.util.Locale
import kotlin.math.abs

/**
 * Переводит DateTime в строку с временем.
 *
 * @param context Контекст приложения/активности
 * @param dateTime DateTime, который нужно перевести в строку
 * @param twelveHoursFormat Нужно ли работаь в 12-и часовом формате?
 * @return Строка с временем, которое было в timestamp.
 */
fun timestampToTimeString(context: Context, dateTime: LocalDateTime, twelveHoursFormat: Boolean): String {
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
    val relativeDateTime = Instant.ofEpochMilli(relativeTimestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
    val requestDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
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

/**
 * Форматирует время в зависимости от заданных параметров
 *
 * @param context Контекст приложения/активности
 * @param relative Относительно чего расчитывать разницу во времени
 * @param twelveHoursFormat Использовать 12-и часовой формат?
 * @param calculateFutureTime Учитывать будущее время?
 * @param fullFormat Форматировать в полном формате?
 * @param dateToLowerCase Преобразовать дату в нижний регистр?
 * @param time Время для форматирования
 */
@Deprecated(message = "Will be removed after merging")
fun formatTime(
        context: Context,
        relative: Long = System.currentTimeMillis(),
        twelveHoursFormat: Boolean = !DateFormat.is24HourFormat(context),
        calculateFutureTime: Boolean = true,
        fullFormat: Boolean = false,
        dateToLowerCase: Boolean = false,
        time: Long
): String {
    val current = (calendarsPool.acquire() ?: getInstance()).apply { timeInMillis = relative }
    val requested = (calendarsPool.acquire() ?: getInstance()).apply { timeInMillis = time }
    val monthName = if (fullFormat) {
        getFullMonthName(context, requested[MONTH] + 1)
    } else {
        getShortMonthName(context, requested[MONTH] + 1)
    }

    val minutesWithLeadingZeros = addLeadingZeroToNumber(requested[MINUTE])
    val requestedTime: String = if (twelveHoursFormat) {
        val amPm = context.getString(if (requested[AM_PM] == 1) {
            R.string.pm
        } else {
            R.string.am
        })

        "${addLeadingZeroToNumber(requested[HOUR])}:$minutesWithLeadingZeros $amPm"
    } else {
        "${addLeadingZeroToNumber(requested[HOUR_OF_DAY])}:$minutesWithLeadingZeros"
    }

    var result: String? = null

    val years = current[YEAR]
    val yearsDiff = years - requested[YEAR]

    if (yearsDiff >= -1 && yearsDiff <= 1) {
        var daysDiff = current[DAY_OF_YEAR] - requested[DAY_OF_YEAR]

        if (yearsDiff == 1 &&
                current[MONTH] == 0 &&
                current[DAY_OF_MONTH] == 1 &&
                requested[MONTH] == 11 &&
                requested[DAY_OF_MONTH] == 31
        ) {
            daysDiff = 1
        } else if (yearsDiff == -1 &&
                current[MONTH] == 11 &&
                current[DAY_OF_MONTH] == 31 &&
                requested[MONTH] == 0 &&
                requested[DAY_OF_MONTH] == 1
        ) {
            daysDiff = -1
        }

        if (daysDiff == 0) {
            val hoursDiff = current[HOUR_OF_DAY] - requested[HOUR_OF_DAY]

            if (hoursDiff == 0) {
                if (current[MINUTE] == requested[MINUTE]) {
                    result = calculateDiff(
                            requested,
                            current,
                            context,
                            fullFormat,
                            calculateFutureTime,
                            SECOND,
                            R.plurals.seconds,
                            R.string.second_mask,
                            dateToLowerCase
                    )
                } else {
                    result = calculateDiff(
                            requested,
                            current,
                            context,
                            fullFormat,
                            calculateFutureTime,
                            MINUTE,
                            R.plurals.minutes,
                            R.string.minute_mask,
                            dateToLowerCase
                    )
                }
            } else if ((fullFormat && hoursDiff <= 12 && hoursDiff >= -12) || (!fullFormat && hoursDiff > 0)) {
                result = calculateDiff(
                        requested,
                        current,
                        context,
                        fullFormat,
                        calculateFutureTime,
                        HOUR_OF_DAY,
                        R.plurals.hours,
                        R.string.hour_mask,
                        dateToLowerCase
                )
            } else if (fullFormat) {
                var todayString = context.getString(R.string.today)

                if (dateToLowerCase) {
                    todayString = todayString.toLowerCase(Locale.getDefault())
                }

                result = context.getString(R.string.at_mask, todayString, requestedTime)
            } else {
                result = requestedTime
            }
        } else if (daysDiff == 1) {
            var yesterdayString = context.getString(R.string.yesterday)

            if (dateToLowerCase) {
                yesterdayString = yesterdayString.toLowerCase(Locale.getDefault())
            }

            result = if (fullFormat) {
                context.getString(R.string.at_mask, yesterdayString, requestedTime)
            } else {
                yesterdayString
            }
        } else if (daysDiff == -1 && calculateFutureTime) {
            var tomorrowString = context.getString(R.string.tomorrow)

            if (dateToLowerCase) {
                tomorrowString = tomorrowString.toLowerCase(Locale.getDefault())
            }

            result = if (fullFormat) {
                context.getString(R.string.at_mask, tomorrowString, requestedTime)
            } else {
                tomorrowString
            }
        } else if (current[WEEK_OF_YEAR] == requested[WEEK_OF_YEAR]) {
            val dayOfWeekDiff = current[DAY_OF_WEEK] - requested[DAY_OF_WEEK]

            if (dayOfWeekDiff >= 0 || (dayOfWeekDiff < 0 && calculateFutureTime)) {
                result = if (fullFormat) {
                    context.getString(R.string.at_mask, getFullNameOfDayOfWeek(context, requested[DAY_OF_WEEK]), requestedTime)
                } else {
                    getShortNameOfDayOfWeek(context, requested[DAY_OF_WEEK])
                }
            }
        }
    }

    calendarsPool.release(current)
    calendarsPool.release(requested)

    return result ?: with(context) {
        if (current[YEAR] != requested[YEAR]) {
            if (fullFormat) {
                getString(R.string.at_mask, getString(R.string.date_mask_with_year,
                        requested[DAY_OF_MONTH],
                        monthName,
                        requested[YEAR]
                ), requestedTime)
            } else {
                getString(R.string.date_mask_with_year, requested[DAY_OF_MONTH], monthName, requested[YEAR])
            }
        } else {
            if (fullFormat) {
                getString(R.string.at_mask, getString(R.string.date_mask_without_year,
                        requested[DAY_OF_MONTH], monthName
                ), requestedTime)
            } else {
                getString(R.string.date_mask_without_year, requested[DAY_OF_MONTH], monthName)
            }
        }
    }
}

private fun calculateDiff(requested: Calendar,
                          current: Calendar,
                          context: Context,
                          fullFormat: Boolean,
                          calculateFutureTime: Boolean,
                          timeUnit: Int,
                          @PluralsRes pluralId: Int,
                          @StringRes timeMaskId: Int,
                          dateToLowerCase: Boolean
): String? {
    val value = requested[timeUnit]
    var valueDiff = current[timeUnit] - value

    var result = with(context.resources) {
        if (valueDiff == 0 && timeUnit == SECOND) {
            context.getString(R.string.just)
        } else if (valueDiff > 0) {
            if (fullFormat) {
                getString(R.string.time_ago_mask, getQuantityString(pluralId, valueDiff, valueDiff))
            } else {
                getString(timeMaskId, valueDiff)
            }
        } else if (calculateFutureTime) {
            valueDiff = abs(valueDiff)

            if (fullFormat) {
                getString(R.string.time_after_mask, getQuantityString(pluralId, valueDiff, valueDiff))
            } else {
                getString(timeMaskId, valueDiff)
            }
        } else {
            null
        }
    }

    if (result != null && dateToLowerCase) {
        result = result.toLowerCase(Locale.getDefault())
    }

    return result
}

internal fun addLeadingZeroToNumber(number: Int): String {
    if (number >= 10) {
        return number.toString()
    }

    return "0$number"
}