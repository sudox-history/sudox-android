package com.sudox.messenger.android.time

import android.content.Context
import android.text.format.DateFormat
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

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
fun formatTime(
        context: Context,
        relative: Long = System.currentTimeMillis(),
        twelveHoursFormat: Boolean = !DateFormat.is24HourFormat(context),
        calculateFutureTime: Boolean = true,
        fullFormat: Boolean = false,
        dateToLowerCase: Boolean = false,
        time: Long
): String {
    val current = (calendarsPool.acquire() ?: Calendar.getInstance()).apply { timeInMillis = relative }
    val requested = (calendarsPool.acquire() ?: Calendar.getInstance()).apply { timeInMillis = time }
    val monthName = if (fullFormat) {
        getFullMonthName(context, requested[Calendar.MONTH] + 1)
    } else {
        getShortMonthName(context, requested[Calendar.MONTH] + 1)
    }

    val minutesWithLeadingZeros = addLeadingZerosToTime(requested[Calendar.MINUTE])
    val requestedTime: String = if (twelveHoursFormat) {
        val amPm = context.getString(if (requested[Calendar.AM_PM] == 1) {
            R.string.pm
        } else {
            R.string.am
        })

        "${addLeadingZerosToTime(requested[Calendar.HOUR])}:$minutesWithLeadingZeros $amPm"
    } else {
        "${addLeadingZerosToTime(requested[Calendar.HOUR_OF_DAY])}:$minutesWithLeadingZeros"
    }

    var result: String? = null

    val years = current[Calendar.YEAR]
    val yearsDiff = years - requested[Calendar.YEAR]

    if (yearsDiff >= -1 && yearsDiff <= 1) {
        var daysDiff = current[Calendar.DAY_OF_YEAR] - requested[Calendar.DAY_OF_YEAR]

        if (yearsDiff == 1 &&
                current[Calendar.MONTH] == 0 &&
                current[Calendar.DAY_OF_MONTH] == 1 &&
                requested[Calendar.MONTH] == 11 &&
                requested[Calendar.DAY_OF_MONTH] == 31
        ) {
            daysDiff = 1
        } else if (yearsDiff == -1 &&
                current[Calendar.MONTH] == 11 &&
                current[Calendar.DAY_OF_MONTH] == 31 &&
                requested[Calendar.MONTH] == 0 &&
                requested[Calendar.DAY_OF_MONTH] == 1
        ) {
            daysDiff = -1
        }

        if (daysDiff == 0) {
            val hoursDiff = current[Calendar.HOUR_OF_DAY] - requested[Calendar.HOUR_OF_DAY]

            if (hoursDiff == 0) {
                if (current[Calendar.MINUTE] == requested[Calendar.MINUTE]) {
                    result = calculateDiff(
                            requested,
                            current,
                            context,
                            fullFormat,
                            calculateFutureTime,
                            Calendar.SECOND,
                            R.plurals.seconds,
                            R.string.second_mask
                    )
                } else {
                    result = calculateDiff(
                            requested,
                            current,
                            context,
                            fullFormat,
                            calculateFutureTime,
                            Calendar.MINUTE,
                            R.plurals.minutes,
                            R.string.minute_mask
                    )
                }
            } else if ((fullFormat && hoursDiff <= 12 && hoursDiff >= -12) || (!fullFormat && hoursDiff > 0)) {
                result = calculateDiff(
                        requested,
                        current,
                        context,
                        fullFormat,
                        calculateFutureTime,
                        Calendar.HOUR_OF_DAY,
                        R.plurals.hours,
                        R.string.hour_mask
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
        } else if (current[Calendar.WEEK_OF_YEAR] == requested[Calendar.WEEK_OF_YEAR]) {
            val dayOfWeekDiff = current[Calendar.DAY_OF_WEEK] - requested[Calendar.DAY_OF_WEEK]

            if (dayOfWeekDiff >= 0 || (dayOfWeekDiff < 0 && calculateFutureTime)) {
                result = if (fullFormat) {
                    context.getString(R.string.at_mask, getFullNameOfDayOfWeek(context, requested[Calendar.DAY_OF_WEEK]), requestedTime)
                } else {
                    getShortNameOfDayOfWeek(context, requested[Calendar.DAY_OF_WEEK])
                }
            }
        }
    }

    calendarsPool.release(current)
    calendarsPool.release(requested)

    return result ?: with(context) {
        if (current[Calendar.YEAR] != requested[Calendar.YEAR]) {
            if (fullFormat) {
                getString(R.string.at_mask, getString(R.string.date_mask_with_year,
                        requested[Calendar.DAY_OF_MONTH],
                        monthName,
                        requested[Calendar.YEAR]
                ), requestedTime)
            } else {
                getString(R.string.date_mask_with_year, requested[Calendar.DAY_OF_MONTH], monthName, requested[Calendar.YEAR])
            }
        } else {
            if (fullFormat) {
                getString(R.string.at_mask, getString(R.string.date_mask_without_year,
                        requested[Calendar.DAY_OF_MONTH], monthName
                ), requestedTime)
            } else {
                getString(R.string.date_mask_without_year, requested[Calendar.DAY_OF_MONTH], monthName)
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
                          @StringRes timeMaskId: Int
): String? {
    val value = requested[timeUnit]
    var valueDiff = current[timeUnit] - value

    with(context.resources) {
        if (valueDiff == 0 && timeUnit == Calendar.SECOND) {
            return context.getString(R.string.just)
        } else if (valueDiff > 0) {
            return if (fullFormat) {
                getString(R.string.time_ago_mask, getQuantityString(pluralId, valueDiff, valueDiff))
            } else {
                getString(timeMaskId, valueDiff)
            }
        } else if (calculateFutureTime) {
            valueDiff = abs(valueDiff)

            return if (fullFormat) {
                getString(R.string.time_after_mask, getQuantityString(pluralId, valueDiff, valueDiff))
            } else {
                getString(timeMaskId, valueDiff)
            }
        }
    }

    return null
}

private fun addLeadingZerosToTime(time: Int): String {
    if (time >= 10) {
        return time.toString()
    }

    return "0$time"
}