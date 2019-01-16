package com.sudox.android.common.helpers

import android.content.Context
import com.sudox.android.R
import java.text.SimpleDateFormat
import java.util.*

val DATE_FORMAT by lazy { SimpleDateFormat("HH:mm dd MMM yyyy D") }

fun formatDate(context: Context, time: Long): String {
    val currentTimeData = DATE_FORMAT.format(Date(System.currentTimeMillis())).split(" ")
    val requestedTimeData = DATE_FORMAT.format(Date(time)).split(" ")

    // Текущая дата
    val currentYear = currentTimeData[3]
    val requestedYear = requestedTimeData[3]
    val currentYearDay = currentTimeData[4]
    val requestedYearDay = requestedTimeData[4]
    val requestedMonthDay = requestedTimeData[1]
    val requestedMonthName = requestedTimeData[2]

    return if (currentYear == requestedYear && currentYearDay == requestedYearDay) {
        requestedTimeData[0]
    } else if (currentYear == requestedYear && requestedYearDay.toInt() == currentYearDay.toInt() - 1) {
        context.getString(R.string.yesterday).toLowerCase()
    } else if (currentYear == requestedYear) {
        "$requestedMonthDay $requestedMonthName"
    } else {
        "$requestedMonthDay $requestedMonthName $requestedYear"
    }
}