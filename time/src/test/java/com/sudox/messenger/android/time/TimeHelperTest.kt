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

        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

    @After
    fun tearDown() {
        calendarsPool = Pools.SimplePool(4)
    }


    @Test
    fun checkSecondsInShortFormatting() {

    }

    @Test
    fun checkSecondsFullFormattingFormat() {
        assertEquals(activity.getString(R.string.just), formatTime(
                activity, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = false, time = 0)
        )

        assertEquals(activity.getString(R.string.time_ago_mask, activity.resources.getQuantityString(R.plurals.seconds, 3, 3)),
                formatTime(
                        activity, 3000, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 0
                )
        )

        assertEquals(activity.getString(R.string.time_after_mask, activity.resources.getQuantityString(R.plurals.seconds, 3, 3)),
                formatTime(
                        activity, 0, twelveHoursFormat = false, calculateFutureTime = true, fullFormat = true, time = 3000
                )
        )

        assertEquals(activity.getString(R.string.date_mask_without_year_and_with_time, 1, getFullMonthName(activity, 1), "00:00"),
                formatTime(
                        activity, 0, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = true, time = 3000
                )
        )

//        assertEquals(activity.getString(R.string.yesterday), formatTime(
//                activity, 0, twelveHoursFormat = false, calculateFutureTime = false, fullFormat = true, time = -60000
//        ))
    }

    @Test
    fun checkSecondsFullFormattingInTwelveHoursFormat() = activity.let {
        assertEquals(it.getString(R.string.just), formatTime(
                it, 0, twelveHoursFormat = true, calculateFutureTime = true, fullFormat = false, time = 0)
        )

        assertEquals(it.getString(R.string.time_ago_mask, it.resources.getQuantityString(R.plurals.seconds, 3, 3)), formatTime(
                it, 3000, twelveHoursFormat = true, calculateFutureTime = true, fullFormat = true, time = 0)
        )

        assertEquals(it.getString(R.string.time_after_mask, it.resources.getQuantityString(R.plurals.seconds, 3, 3)), formatTime(
                it, 0, twelveHoursFormat = true, calculateFutureTime = true, fullFormat = true, time = 3000
        ))

        assertEquals(it.getString(R.string.date_mask_without_year_and_with_time, 1, getFullMonthName(it, 1),
                "00:00 ${it.getString(R.string.am)}"), formatTime(
                it, 0, twelveHoursFormat = true, calculateFutureTime = false, fullFormat = true, time = 3000
        ))

//        assertEquals(it.getString(R.string.yesterday), formatTime(
//                it, 0, twelveHoursFormat = true, calculateFutureTime = false, fullFormat = true, time = -60000
//        ))
    }
}