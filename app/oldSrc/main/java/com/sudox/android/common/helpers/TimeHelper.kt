package com.sudox.android.common.helpers

import android.content.Context
import android.text.format.DateFormat
import com.sudox.android.R
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMAT by lazy { SimpleDateFormat("HH:mm dd MMM yyyy D a KK") }

/**
 * Форматирует дату.
 *
 * 1) Если год одинаковый и день тоже одинаковый, то выводится время;
 * 2) Если год одинаковый и день уже вчерашний, то выводится фраза "вчера";
 * 3) Если год одинаковый, но день позавчерашний и более, то выводится фраза, подобная этой "31 янв"
 * 4) Если года разные и день позавчерашний и более, то выводится фраза, подобная этой "31 янв 2017"
 */
fun formatDate(context: Context, time: Long): String {
    val currentTimeData = DATE_FORMAT.format(Date(System.currentTimeMillis())).split(" ")
    val requestedTimeData = DATE_FORMAT.format(Date(time)).split(" ")

    // Текущая дата
    val currentYear = currentTimeData[3].toInt()
    val requestedYear = requestedTimeData[3].toInt()
    val currentYearDay = currentTimeData[4].toInt()
    val requestedYearDay = requestedTimeData[4].toInt()
    val requestedMonthDay = requestedTimeData[1].toInt()
    val requestedMonthName = requestedTimeData[2]
    var requestedTime = requestedTimeData[0]
    val requestedPartOfDay = requestedTimeData[5]

    if (!DateFormat.is24HourFormat(context)) {
        val hours =  requestedTimeData[6]
        val minutes = requestedTime.split(":")[1]

        // Переводим время в 12-и часовую систему если в системе включена соответствующая функция
        requestedTime = "$hours:$minutes $requestedPartOfDay"
    }

    return if (currentYear == requestedYear && currentYearDay == requestedYearDay) {
        requestedTime
    } else if ((currentYear == requestedYear && requestedYearDay == currentYearDay - 1) || (currentYear - requestedYear == 1 && currentYearDay == 1)) {
        context.getString(R.string.yesterday).toLowerCase()
    } else if (currentYear == requestedYear) {
        "$requestedMonthDay $requestedMonthName"
    } else {
        "$requestedMonthDay $requestedMonthName $requestedYear"
    }
}