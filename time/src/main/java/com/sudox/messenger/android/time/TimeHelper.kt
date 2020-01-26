package com.sudox.messenger.android.time

import android.content.Context
import android.text.format.DateFormat
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import java.util.Calendar
import kotlin.math.abs

fun formatTime(
        context: Context,
        relative: Long = System.currentTimeMillis(),
        twelveHoursFormat: Boolean = DateFormat.is24HourFormat(context),
        calculateFutureTime: Boolean = true,
        fullFormat: Boolean = false,
        time: Long
): String? {
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

    if (current[Calendar.YEAR] == requested[Calendar.YEAR]) {
        val daysDiff = current[Calendar.DAY_OF_YEAR] - requested[Calendar.DAY_OF_YEAR]

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
            } else if (hoursDiff <= 12 && hoursDiff >= -12) {
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
                result = context.getString(R.string.at_mask, context.getString(R.string.today), requestedTime)
            } else {
                result = context.getString(R.string.today)
            }
        } else if (daysDiff == 1) {
            result = if (fullFormat) {
                context.getString(R.string.at_mask, context.getString(R.string.yesterday), requestedTime)
            } else {
                context.getString(R.string.yesterday)
            }
        } else if (daysDiff == -1 && calculateFutureTime) {
            result = if (fullFormat) {
                context.getString(R.string.at_mask, context.getString(R.string.tomorrow), requestedTime)
            } else {
                context.getString(R.string.tomorrow)
            }
        } else if (current[Calendar.WEEK_OF_YEAR] == requested[Calendar.WEEK_OF_YEAR]) {
            val dayOfWeekDiff = current[Calendar.WEEK_OF_YEAR] - requested[Calendar.WEEK_OF_YEAR]

            if (dayOfWeekDiff >= 0 || (dayOfWeekDiff < 0 && calculateFutureTime)) {
                if (fullFormat) {
                    context.getString(R.string.at_mask, getFullMonthName(context, requested[Calendar.WEEK_OF_YEAR]), requestedTime)
                } else {
                    getShortMonthName(context, requested[Calendar.WEEK_OF_YEAR])
                }
            }
        }
    }

    calendarsPool.release(current)
    calendarsPool.release(requested)

    return result ?: with(context) {
        if (current[Calendar.YEAR] != requested[Calendar.YEAR]) {
            if (fullFormat) {
                getString(R.string.date_mask_with_year_and_time,
                        requested[Calendar.DAY_OF_MONTH],
                        monthName,
                        requested[Calendar.YEAR],
                        requestedTime
                )
            } else {
                getString(R.string.date_mask_with_year, requested[Calendar.DAY_OF_MONTH], monthName, current[Calendar.YEAR])
            }
        } else {
            if (fullFormat) {
                getString(R.string.date_mask_without_year_and_with_time, requested[Calendar.DAY_OF_MONTH], monthName, requestedTime)
            } else {
                getString(R.string.date_mask_without_year_and_time, requested[Calendar.DAY_OF_MONTH], monthName)
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