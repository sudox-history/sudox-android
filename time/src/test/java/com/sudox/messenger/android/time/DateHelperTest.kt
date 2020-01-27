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
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
class DateHelperTest : Assert() {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity
    private lateinit var view: View

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()
        view = View(activity)

        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    }

    @After
    fun tearDown() {
        calendarsPool = Pools.SimplePool(4)
    }

    @Test
    fun checkShortMonthNames() {
        assertNull(getShortMonthName(activity, 0))
        assertNull(getShortMonthName(activity, 13))

        assertEquals(getShortMonthName(activity, 1), activity.getString(R.string.january_short))
        assertEquals(getShortMonthName(activity, 2), activity.getString(R.string.february_short))
        assertEquals(getShortMonthName(activity, 3), activity.getString(R.string.march_short))
        assertEquals(getShortMonthName(activity, 4), activity.getString(R.string.april_short))
        assertEquals(getShortMonthName(activity, 5), activity.getString(R.string.may_short))
        assertEquals(getShortMonthName(activity, 6), activity.getString(R.string.june_short))
        assertEquals(getShortMonthName(activity, 7), activity.getString(R.string.july_short))
        assertEquals(getShortMonthName(activity, 8), activity.getString(R.string.august_short))
        assertEquals(getShortMonthName(activity, 9), activity.getString(R.string.september_short))
        assertEquals(getShortMonthName(activity, 10), activity.getString(R.string.october_short))
        assertEquals(getShortMonthName(activity, 11), activity.getString(R.string.november_short))
        assertEquals(getShortMonthName(activity, 12), activity.getString(R.string.december_short))
    }

    @Test
    fun checkFullMonthNames() {
        assertNull(getFullMonthName(activity, 0))
        assertNull(getFullMonthName(activity, 13))

        assertEquals(getFullMonthName(activity, 1), activity.getString(R.string.january))
        assertEquals(getFullMonthName(activity, 2), activity.getString(R.string.february))
        assertEquals(getFullMonthName(activity, 3), activity.getString(R.string.march))
        assertEquals(getFullMonthName(activity, 4), activity.getString(R.string.april))
        assertEquals(getFullMonthName(activity, 5), activity.getString(R.string.may))
        assertEquals(getFullMonthName(activity, 6), activity.getString(R.string.june))
        assertEquals(getFullMonthName(activity, 7), activity.getString(R.string.july))
        assertEquals(getFullMonthName(activity, 8), activity.getString(R.string.august))
        assertEquals(getFullMonthName(activity, 9), activity.getString(R.string.september))
        assertEquals(getFullMonthName(activity, 10), activity.getString(R.string.october))
        assertEquals(getFullMonthName(activity, 11), activity.getString(R.string.november))
        assertEquals(getFullMonthName(activity, 12), activity.getString(R.string.december))
    }

    @Test
    fun checkFullDaysOfWeekNames() {
        assertNull(getFullNameOfDayOfWeek(activity, 0))
        assertNull(getFullNameOfDayOfWeek(activity, 8))

        assertEquals(activity.getString(R.string.sunday), getFullNameOfDayOfWeek(activity, 1))
        assertEquals(activity.getString(R.string.monday), getFullNameOfDayOfWeek(activity, 2))
        assertEquals(activity.getString(R.string.tuesday), getFullNameOfDayOfWeek(activity, 3))
        assertEquals(activity.getString(R.string.wednesday), getFullNameOfDayOfWeek(activity, 4))
        assertEquals(activity.getString(R.string.thursday), getFullNameOfDayOfWeek(activity, 5))
        assertEquals(activity.getString(R.string.friday), getFullNameOfDayOfWeek(activity, 6))
        assertEquals(activity.getString(R.string.saturday), getFullNameOfDayOfWeek(activity, 7))
    }

    @Test
    fun checkShortDaysOfWeekNames() {
        assertNull(getShortNameOfDayOfWeek(activity, 0))
        assertNull(getShortNameOfDayOfWeek(activity, 8))

        assertEquals(activity.getString(R.string.sunday_short), getShortNameOfDayOfWeek(activity, 1))
        assertEquals(activity.getString(R.string.monday_short), getShortNameOfDayOfWeek(activity, 2))
        assertEquals(activity.getString(R.string.tuesday_short), getShortNameOfDayOfWeek(activity, 3))
        assertEquals(activity.getString(R.string.wednesday_short), getShortNameOfDayOfWeek(activity, 4))
        assertEquals(activity.getString(R.string.thursday_short), getShortNameOfDayOfWeek(activity, 5))
        assertEquals(activity.getString(R.string.friday_short), getShortNameOfDayOfWeek(activity, 6))
        assertEquals(activity.getString(R.string.saturday_short), getShortNameOfDayOfWeek(activity, 7))
    }
}