package com.sudox.messenger.android.time

import android.content.Context
import androidx.core.util.Pools
import java.util.Calendar
import kotlin.math.abs

internal var calendarsPool = Pools.SimplePool<Calendar>(4)

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

fun getShortNameOfDayOfWeek(context: Context, number: Int): String? {
    if (number < 1 || number > 7) {
        return null
    }

    return when (translateWeekDay(number)) {
        1 -> context.getString(R.string.monday_short)
        2 -> context.getString(R.string.tuesday_short)
        3 -> context.getString(R.string.wednesday_short)
        4 -> context.getString(R.string.thursday_short)
        5 -> context.getString(R.string.friday_short)
        6 -> context.getString(R.string.saturday_short)
        7 -> context.getString(R.string.sunday_short)
        else -> null
    }
}

fun getFullNameOfDayOfWeek(context: Context, number: Int): String? {
    if (number < 1 || number > 7) {
        return null
    }

    return when (translateWeekDay(number)) {
        1 -> context.getString(R.string.monday)
        2 -> context.getString(R.string.tuesday)
        3 -> context.getString(R.string.wednesday)
        4 -> context.getString(R.string.thursday)
        5 -> context.getString(R.string.friday)
        6 -> context.getString(R.string.saturday)
        7 -> context.getString(R.string.sunday)
        else -> null
    }
}

private fun translateWeekDay(number: Int): Int {
    val calendar = calendarsPool.acquire() ?: Calendar.getInstance()
    var weekDayNumber = number - (Calendar.MONDAY - calendar.firstDayOfWeek)

    if (weekDayNumber > 7) {
        weekDayNumber -= 7
    } else if (weekDayNumber <= 0) {
        weekDayNumber = 7 - abs(weekDayNumber)
    }

    calendarsPool.release(calendar)

    return weekDayNumber
}