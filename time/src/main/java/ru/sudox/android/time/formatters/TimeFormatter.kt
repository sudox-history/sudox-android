package ru.sudox.android.time.formatters

import android.content.Context
import org.threeten.bp.LocalDateTime

interface TimeFormatter {
    fun onEventOccurredInAnotherYear(context: Context, yearsAgo: Int, request: LocalDateTime, relative: LocalDateTime): String
    fun onEventOccurredInSameYear(context: Context, request: LocalDateTime, relative: LocalDateTime): String
    fun onEventOccurredBetween2And7DaysAgo(context: Context, daysAgo: Int, request: LocalDateTime, relative: LocalDateTime): String?
    fun onEventOccurredYesterday(context: Context, request: LocalDateTime, relative: LocalDateTime): String
    fun onEventOccurredBetween12And24HoursAgo(context: Context, hoursAgo: Int, request: LocalDateTime, relative: LocalDateTime): String
    fun onEventOccurredBetween1And12HoursAgo(context: Context, hoursAgo: Int, request: LocalDateTime, relative: LocalDateTime): String
    fun onEventOccurredBetween1And60MinutesAgo(context: Context, minutesAgo: Int, request: LocalDateTime, relative: LocalDateTime): String
    fun onEventOccurredNow(context: Context, request: LocalDateTime, relative: LocalDateTime): String
}