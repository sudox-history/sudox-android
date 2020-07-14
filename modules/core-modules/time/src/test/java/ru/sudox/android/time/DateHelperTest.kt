package ru.sudox.android.time

import android.app.Activity
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.Assert.*
import org.robolectric.Robolectric
import java.time.DayOfWeek
import java.time.Month
import java.util.TimeZone

@Config(qualifiers = "en")
@RunWith(RobolectricTestRunner::class)
class DateHelperTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()

    init {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    }

    @Test
    fun testGetFullNameOfDayOfWeek() {
        assertEquals("Saturday", getFullNameOfDayOfWeek(activity, DayOfWeek.SATURDAY))
        assertEquals("Monday", getFullNameOfDayOfWeek(activity, DayOfWeek.MONDAY))
        assertEquals("Tuesday", getFullNameOfDayOfWeek(activity, DayOfWeek.TUESDAY))
        assertEquals("Wednesday", getFullNameOfDayOfWeek(activity, DayOfWeek.WEDNESDAY))
        assertEquals("Thursday", getFullNameOfDayOfWeek(activity, DayOfWeek.THURSDAY))
        assertEquals("Friday", getFullNameOfDayOfWeek(activity, DayOfWeek.FRIDAY))
        assertEquals("Sunday", getFullNameOfDayOfWeek(activity, DayOfWeek.SUNDAY))
    }

    @Test
    fun testGetShortNameOfDayOfWeek() {
        assertEquals("sat", getShortNameOfDayOfWeek(activity, DayOfWeek.SATURDAY))
        assertEquals("mon", getShortNameOfDayOfWeek(activity, DayOfWeek.MONDAY))
        assertEquals("tue", getShortNameOfDayOfWeek(activity, DayOfWeek.TUESDAY))
        assertEquals("wed", getShortNameOfDayOfWeek(activity, DayOfWeek.WEDNESDAY))
        assertEquals("thu", getShortNameOfDayOfWeek(activity, DayOfWeek.THURSDAY))
        assertEquals("fri", getShortNameOfDayOfWeek(activity, DayOfWeek.FRIDAY))
        assertEquals("sun", getShortNameOfDayOfWeek(activity, DayOfWeek.SUNDAY))
    }

    @Test
    fun testGetFullNameOfMonth() {
        assertEquals("January", getFullNameOfMonth(activity, Month.JANUARY))
        assertEquals("February", getFullNameOfMonth(activity, Month.FEBRUARY))
        assertEquals("March", getFullNameOfMonth(activity, Month.MARCH))
        assertEquals("April", getFullNameOfMonth(activity, Month.APRIL))
        assertEquals("May", getFullNameOfMonth(activity, Month.MAY))
        assertEquals("June", getFullNameOfMonth(activity, Month.JUNE))
        assertEquals("July", getFullNameOfMonth(activity, Month.JULY))
        assertEquals("August", getFullNameOfMonth(activity, Month.AUGUST))
        assertEquals("September", getFullNameOfMonth(activity, Month.SEPTEMBER))
        assertEquals("October", getFullNameOfMonth(activity, Month.OCTOBER))
        assertEquals("November", getFullNameOfMonth(activity, Month.NOVEMBER))
        assertEquals("December", getFullNameOfMonth(activity, Month.DECEMBER))
    }

    @Test
    fun testGetShortNameOfMonth() {
        assertEquals("jan", getShortNameOfMonth(activity, Month.JANUARY))
        assertEquals("feb", getShortNameOfMonth(activity, Month.FEBRUARY))
        assertEquals("mar", getShortNameOfMonth(activity, Month.MARCH))
        assertEquals("apr", getShortNameOfMonth(activity, Month.APRIL))
        assertEquals("may", getShortNameOfMonth(activity, Month.MAY))
        assertEquals("jun", getShortNameOfMonth(activity, Month.JUNE))
        assertEquals("jul", getShortNameOfMonth(activity, Month.JULY))
        assertEquals("aug", getShortNameOfMonth(activity, Month.AUGUST))
        assertEquals("sep", getShortNameOfMonth(activity, Month.SEPTEMBER))
        assertEquals("oct", getShortNameOfMonth(activity, Month.OCTOBER))
        assertEquals("nov", getShortNameOfMonth(activity, Month.NOVEMBER))
        assertEquals("dec", getShortNameOfMonth(activity, Month.DECEMBER))
    }

    @Test
    fun testTimestampToString() {
        assertEquals("January 1", timestampToDateString(activity, 0L, 0L))
        assertEquals("January 1 1970", timestampToDateString(activity, 31536000000L, 0L))
    }
}