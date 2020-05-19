package ru.sudox.android.time.formatters

import android.content.Context
import org.threeten.bp.LocalDateTime
import ru.sudox.android.time.R
import ru.sudox.android.time.getShortNameOfDayOfWeek
import ru.sudox.android.time.getShortNameOfMonth

object ShortTimeFormatter : TimeFormatter {

    override fun onEventOccurredInAnotherYear(context: Context, yearsAgo: Int, request: LocalDateTime, relative: LocalDateTime, useTwelveHoursFormat: Boolean): String {
        return context.getString(R.string.short_formatter_years, yearsAgo)
    }

    override fun onEventOccurredInSameYear(context: Context, request: LocalDateTime, relative: LocalDateTime, useTwelveHoursFormat: Boolean): String {
        return context.getString(R.string.short_formatter_same_year, request.dayOfMonth, getShortNameOfMonth(context, request.month))
    }

    override fun onEventOccurredBetween2And7DaysAgo(context: Context, daysAgo: Int, request: LocalDateTime, relative: LocalDateTime, useTwelveHoursFormat: Boolean): String {
        return getShortNameOfDayOfWeek(context, request.dayOfWeek)
    }

    override fun onEventOccurredYesterday(context: Context, request: LocalDateTime, relative: LocalDateTime, useTwelveHoursFormat: Boolean): String {
        return context.getString(R.string.short_formatter_yesterday)
    }

    override fun onEventOccurredBetween12And24HoursAgo(context: Context, hoursAgo: Int, request: LocalDateTime, relative: LocalDateTime, useTwelveHoursFormat: Boolean): String {
        return context.getString(R.string.short_formatter_hours, hoursAgo)
    }

    override fun onEventOccurredBetween1And12HoursAgo(context: Context, hoursAgo: Int, request: LocalDateTime, relative: LocalDateTime, useTwelveHoursFormat: Boolean): String {
        return context.getString(R.string.short_formatter_hours, hoursAgo)
    }

    override fun onEventOccurredBetween1And60MinutesAgo(context: Context, minutesAgo: Int, request: LocalDateTime, relative: LocalDateTime, useTwelveHoursFormat: Boolean): String {
        return context.getString(R.string.short_formatter_minutes, minutesAgo)
    }

    override fun onEventOccurredNow(context: Context, request: LocalDateTime, relative: LocalDateTime, useTwelveHoursFormat: Boolean): String {
        return context.getString(R.string.short_formatter_now)
    }
}