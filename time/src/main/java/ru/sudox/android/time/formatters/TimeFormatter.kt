package ru.sudox.android.time.formatters

import android.content.Context

interface TimeFormatter {
    fun formatWhenEventOccurredInAnotherYear(context: Context, yearsAgo: Int, timestamp: Long): String
    fun formatWhenEventOccurredInSameYear(context: Context, timestamp: Long): String
    fun formatWhenEventOccurredBetween2And7DaysAgo(context: Context, daysAgo: Int, timestamp: Long): String
    fun formatWhenEventOccurredYesterday(context: Context, timestamp: Long): String
    fun formatWhenEventOccurredBetween12And24HoursAgo(context: Context, hoursAgo: Int, timestamp: Long): String
    fun formatWhenEventOccurredBetween1And12HoursAgo(context: Context, hoursAgo: Int, timestamp: Long): String
    fun formatWhenEventOccurredBetween1And60MinutesAgo(context: Context, minutesAgo: Int, timestamp: Long): String
    fun formatWhenEventOccurredNow(context: Context): String
}