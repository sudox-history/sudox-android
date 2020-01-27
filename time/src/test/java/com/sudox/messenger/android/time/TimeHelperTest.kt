package com.sudox.messenger.android.time

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.core.util.Pools
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import java.util.Locale
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
class TimeHelperTest : Assert() {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity
    private lateinit var view: View

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()
        view = View(activity)

        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
        Locale.setDefault(Locale("EN", "GB"))
    }

    @After
    fun tearDown() {
        calendarsPool = Pools.SimplePool(4)
    }

    @Test
    fun checkShortWeekOfDaysFormatting() = activity.let {
        assertEquals(it.getString(R.string.monday_short), formatTime(
                it, 545400000, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = false, time = 372600000
        ))

        assertEquals(it.getString(R.string.wednesday_short), formatTime(
                it, 372600000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 545400000
        ))
    }

    @Test
    fun checkFullWeekOfDaysFormatting() = activity.let {
        assertEquals(it.getString(R.string.at_mask, it.getString(R.string.monday), "07:30"), formatTime(
                it, 545400000, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = true, time = 372600000
        ))

        assertEquals(it.getString(R.string.at_mask, it.getString(R.string.wednesday), "07:30"), formatTime(
                it, 372600000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 545400000
        ))
    }

    @Test
    fun checkTwelveHoursTimeFormatting() = activity.let {
        assertEquals("00:30 ${it.getString(R.string.pm)}", formatTime(
                it, 0, twelveHoursFormat = true, calculateFutureTime = true, fullFormat = false, time = 45000000
        ))

        assertEquals("07:30 ${it.getString(R.string.am)}", formatTime(
                it, 0, twelveHoursFormat = true, calculateFutureTime = true, fullFormat = false, time = 27000000
        ))
    }

    @Test
    fun checkFullTimeFormatting() = activity.let {
        assertEquals(it.getString(R.string.time_ago_mask, it.resources.getQuantityString(R.plurals.hours, 12, 12)),
                formatTime(
                        it, 84600000, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = true, time = 41400000
                ))

        assertEquals(it.getString(R.string.time_after_mask, it.resources.getQuantityString(R.plurals.hours, 12, 12)),
                formatTime(
                        it, 41400000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 84600000
                ))

        assertEquals(it.getString(R.string.at_mask, it.getString(R.string.today), "10:30"), formatTime(
                it, 84600000, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = true, time = 37800000
        ))

        assertEquals(it.getString(R.string.at_mask, it.getString(R.string.today), "01:00"), formatTime(
                it, 54000000, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = true, time = 3600000
        ))

        assertEquals(it.getString(R.string.at_mask, it.getString(R.string.today), "13:30"), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 48600000
        ))
    }

    @Test
    fun checkShortTimeFormatting() = activity.let {
        assertEquals(it.getString(R.string.hour_mask, 13), formatTime(
                it, 84600000, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = false, time = 37800000
        ))

        assertEquals(it.getString(R.string.hour_mask, 14), formatTime(
                it, 54000000, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = false, time = 3600000
        ))

        assertEquals("13:30", formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 48600000
        ))
    }
    @Test
    fun checkDaysShortFormatting() = activity.let {
        assertEquals(it.getString(
                R.string.date_mask_without_year,
                1, getShortMonthName(it, 1)
        ), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = false, time = 3000
        ))

        assertEquals(it.getString(
                R.string.date_mask_with_year,
                1, getShortMonthName(it, 1),
                1970
        ), formatTime(
                it, 63118800000, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = false, time = 0
        ))

        assertEquals(it.getString(R.string.yesterday), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = false, time = -3000
        ))

        assertEquals(it.getString(R.string.tomorrow), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 86400000
        ))

        assertEquals(it.getString(R.string.tomorrow), formatTime(
                it, -3000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 0
        ))
    }

    @Test
    fun checkDaysFullFormatting() = activity.let {
        assertEquals(it.getString(R.string.at_mask, it.getString(
                R.string.date_mask_without_year,
                1, getFullMonthName(it, 1)),
                "00:00"
        ), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = true, time = 3000
        ))

        assertEquals(it.getString(R.string.at_mask, it.getString(
                R.string.date_mask_with_year,
                1, getFullMonthName(it, 1),
                1970),
                "00:00"
        ), formatTime(
                it, 63118800000, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = true, time = 0
        ))

        assertEquals(it.getString(R.string.at_mask, it.getString(R.string.yesterday), "23:59"), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = true, time = -3000
        ))

        assertEquals(it.getString(R.string.at_mask, it.getString(R.string.tomorrow), "00:00"), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 86400000
        ))

        assertEquals(it.getString(R.string.at_mask, it.getString(R.string.tomorrow), "00:00"), formatTime(
                it, -3000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 0
        ))
    }

    @Test
    fun checkHoursShortFormatting() = activity.let {
        assertEquals(it.getString(R.string.hour_mask, 12), formatTime(
                it, 43200000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 0
        ))

        assertEquals("12:00", formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 43200000
        ))

        assertEquals(it.getString(R.string.hour_mask, 13), formatTime(
                it, 46800000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 0
        ))

        assertEquals("13:00", formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 46800000
        ))
    }

    @Test
    fun checkHoursFullFormatting() = activity.let {
        assertEquals(it.getString(R.string.time_ago_mask, it.resources.getQuantityString(R.plurals.hours, 12, 12)), formatTime(
                it, 43200000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 0
        ))

        assertEquals(it.getString(R.string.time_after_mask, it.resources.getQuantityString(R.plurals.hours, 12, 12)), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 43200000
        ))

        assertEquals(it.getString(R.string.at_mask, it.getString(R.string.today), "00:00"), formatTime(
                it, 46800000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 0
        ))

        assertEquals(it.getString(R.string.at_mask, it.getString(R.string.today), "13:00"), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 46800000
        ))
    }

    @Test
    fun checkMinutesShortFormatting() = activity.let {
        assertEquals(it.getString(R.string.minute_mask, 3), formatTime(
                it, 180000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 0
        ))

        assertEquals(it.getString(R.string.minute_mask, 3), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 180000
        ))
    }

    @Test
    fun checkMinutesFullFormatting() = activity.let {
        assertEquals(it.getString(R.string.time_ago_mask, it.resources.getQuantityString(R.plurals.minutes, 3, 3)), formatTime(
                it, 180000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 0
        ))

        assertEquals(it.getString(R.string.time_after_mask, it.resources.getQuantityString(R.plurals.minutes, 3, 3)), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 180000
        ))
    }

    @Test
    fun checkSecondsShortFormatting() = activity.let {
        assertEquals(it.getString(R.string.just), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 0
        ))

        assertEquals(it.getString(R.string.second_mask, 3), formatTime(
                it, 3000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 0
        ))

        assertEquals(it.getString(R.string.second_mask, 3), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 3000
        ))
    }

    @Test
    fun checkSecondsFullFormatting() = activity.let {
        assertEquals(it.getString(R.string.just), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 0
        ))

        assertEquals(it.getString(R.string.time_ago_mask, it.resources.getQuantityString(R.plurals.seconds, 3, 3)), formatTime(
                it, 3000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 0
        ))

        assertEquals(it.getString(R.string.time_after_mask, it.resources.getQuantityString(R.plurals.seconds, 3, 3)), formatTime(
                it, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 3000
        ))
    }
}