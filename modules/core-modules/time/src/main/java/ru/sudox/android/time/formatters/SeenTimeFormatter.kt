package ru.sudox.android.time.formatters

import android.content.Context
import ru.sudox.android.time.*
import java.time.LocalDateTime

object SeenTimeFormatter : TimeFormatter {

    override fun onEventOccurredInAnotherYear(
        context: Context,
        yearsAgo: Int,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String {
        val monthName = getShortNameOfMonth(context, request.month)
        val timeString = timestampToTimeString(context, request, useTwelveHoursFormat)

        return context.getString(R.string.seen_time_formatter_in_another_year, monthName, request.dayOfMonth, request.year, timeString)
    }

    override fun onEventOccurredInSameYear(
        context: Context,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String {
        val monthName = getFullNameOfMonth(context, request.month)
        val timeString = timestampToTimeString(context, request, useTwelveHoursFormat)

        return context.getString(R.string.seen_time_formatter_in_same_year, monthName, request.dayOfMonth, timeString)
    }

    override fun onEventOccurredBetween2And7DaysAgo(
        context: Context,
        daysAgo: Int,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String {
        val dayOfWeekName = getFullNameOfDayOfWeek(context, request.dayOfWeek)
        val timeString = timestampToTimeString(context, request, useTwelveHoursFormat)

        return context.getString(R.string.seen_time_formatter_on_week, dayOfWeekName, timeString)
    }

    override fun onEventOccurredYesterday(
        context: Context,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String =
        context.getString(R.string.seen_time_formatter_yesterday, timestampToTimeString(context, request, useTwelveHoursFormat))

    override fun onEventOccurredBetween12And24HoursAgo(
        context: Context,
        hoursAgo: Int,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String = context.getString(R.string.seen_time_at, timestampToTimeString(context, request, useTwelveHoursFormat))

    override fun onEventOccurredBetween1And12HoursAgo(
        context: Context,
        hoursAgo: Int,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String = context.resources.getQuantityString(R.plurals.seen_time_formatter_hours, hoursAgo, hoursAgo)

    override fun onEventOccurredBetween1And60MinutesAgo(
        context: Context,
        minutesAgo: Int,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String = context.resources.getQuantityString(R.plurals.seen_time_formatter_minutes, minutesAgo, minutesAgo)

    override fun onEventOccurredNow(
        context: Context,
        request: LocalDateTime,
        relative: LocalDateTime,
        useTwelveHoursFormat: Boolean
    ): String = context.getString(R.string.seen_time_formatter_now)
}