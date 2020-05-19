package ru.sudox.android.time

import android.app.Activity
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.sudox.android.time.formatters.TimeFormatter
import java.util.TimeZone

@Config(qualifiers = "en")
@RunWith(RobolectricTestRunner::class)
class TimeHelperTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()

    init {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    }

    @Test
    fun checkTimestampToTimeStringIn24HoursFormat() {
        assertEquals("00:00", timestampToTimeString(activity, dateTimeOf(0), false))
        assertEquals("20:00", timestampToTimeString(activity, dateTimeOf(72000000), false))
        assertEquals("00:20", timestampToTimeString(activity, dateTimeOf(1815600000), false))
    }

    @Test
    fun checkTimestampToTimeStringIn12HoursFormat() {
        assertEquals("00:00 AM", timestampToTimeString(activity, dateTimeOf(0), true))
        assertEquals("08:00 PM", timestampToTimeString(activity, dateTimeOf(72000000), true))
        assertEquals("00:20 AM", timestampToTimeString(activity, dateTimeOf(1815600000), true))
    }

    @Test
    fun checkTimestampToStringWhenSecondsAgo() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredNow(any(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 5000L, formatter, 0L)
        verify(formatter).onEventOccurredNow(activity, dateTimeOf(0L), dateTimeOf(5000L))
    }

    @Test
    fun checkTimestampToStringWhenSecondsAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredNow(any(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 5000L, formatter, -1000L)
        verify(formatter).onEventOccurredNow(activity, dateTimeOf(-1000L), dateTimeOf(5000L))
    }

    @Test
    fun checkTimestampToStringWhenMinutesAgo() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredBetween1And60MinutesAgo(any(), anyInt(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 600000L, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And60MinutesAgo(activity, 10, dateTimeOf(0L), dateTimeOf(600000L))
    }

    @Test
    fun checkTimestampToStringWhenMinutesAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredBetween1And60MinutesAgo(any(), anyInt(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 540000L, formatter, -60000L)
        verify(formatter).onEventOccurredBetween1And60MinutesAgo(activity, 10, dateTimeOf(-60000L), dateTimeOf(540000L))
    }

    @Test
    fun checkTimestampToStringWhenTwelveOrLessHoursAgo() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredBetween1And12HoursAgo(any(), anyInt(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 12 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 12, dateTimeOf(0L), dateTimeOf(12 * 60 * 60 * 1000))

        timestampToString(activity, 11 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 11, dateTimeOf(0L), dateTimeOf(11 * 60 * 60 * 1000))

        timestampToString(activity, 10 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 10, dateTimeOf(0L), dateTimeOf(10 * 60 * 60 * 1000))

        timestampToString(activity, 9 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 9, dateTimeOf(0L), dateTimeOf(9 * 60 * 60 * 1000))

        timestampToString(activity, 8 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 8, dateTimeOf(0L), dateTimeOf(8 * 60 * 60 * 1000))

        timestampToString(activity, 7 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 7, dateTimeOf(0L), dateTimeOf(7 * 60 * 60 * 1000))

        timestampToString(activity, 6 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 6, dateTimeOf(0L), dateTimeOf(6 * 60 * 60 * 1000))

        timestampToString(activity, 5 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 5, dateTimeOf(0L), dateTimeOf(5 * 60 * 60 * 1000))

        timestampToString(activity, 4 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 4, dateTimeOf(0L), dateTimeOf(4 * 60 * 60 * 1000))

        timestampToString(activity, 3 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 3, dateTimeOf(0L), dateTimeOf(3 * 60 * 60 * 1000))

        timestampToString(activity, 2 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 2, dateTimeOf(0L), dateTimeOf(2 * 60 * 60 * 1000))

        timestampToString(activity, 1 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 1, dateTimeOf(0L), dateTimeOf(1 * 60 * 60 * 1000))
    }

    @Test
    fun checkTimestampToStringWhenTwelveOrLessHoursAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredBetween1And12HoursAgo(any(), anyInt(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 11 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 12, dateTimeOf(-3600000L), dateTimeOf(11 * 60 * 60 * 1000))

        timestampToString(activity, 10 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 11, dateTimeOf(-3600000L), dateTimeOf(10 * 60 * 60 * 1000))

        timestampToString(activity, 9 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 10, dateTimeOf(-3600000L), dateTimeOf(9 * 60 * 60 * 1000))

        timestampToString(activity, 8 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 9, dateTimeOf(-3600000L), dateTimeOf(8 * 60 * 60 * 1000))

        timestampToString(activity, 7 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 8, dateTimeOf(-3600000L), dateTimeOf(7 * 60 * 60 * 1000))

        timestampToString(activity, 6 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 7, dateTimeOf(-3600000L), dateTimeOf(6 * 60 * 60 * 1000))

        timestampToString(activity, 5 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 6, dateTimeOf(-3600000L), dateTimeOf(5 * 60 * 60 * 1000))

        timestampToString(activity, 4 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 5, dateTimeOf(-3600000L), dateTimeOf(4 * 60 * 60 * 1000))

        timestampToString(activity, 3 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 4, dateTimeOf(-3600000L), dateTimeOf(3 * 60 * 60 * 1000))

        timestampToString(activity, 2 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 3, dateTimeOf(-3600000L), dateTimeOf(2 * 60 * 60 * 1000))

        timestampToString(activity, 1 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 2, dateTimeOf(-3600000L), dateTimeOf(1 * 60 * 60 * 1000))

        timestampToString(activity, 0L, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween1And12HoursAgo(activity, 1, dateTimeOf(-3600000L), dateTimeOf(0))
    }

    @Test
    fun checkTimestampToStringWhenTwentyThreeOrLessHoursAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredBetween12And24HoursAgo(any(), anyInt(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 22 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 23, dateTimeOf(-3600000L), dateTimeOf(22 * 60 * 60 * 1000))

        timestampToString(activity, 21 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 22, dateTimeOf(-3600000L), dateTimeOf(21 * 60 * 60 * 1000))

        timestampToString(activity, 20 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 21, dateTimeOf(-3600000L), dateTimeOf(20 * 60 * 60 * 1000))

        timestampToString(activity, 19 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 20, dateTimeOf(-3600000L), dateTimeOf(19 * 60 * 60 * 1000))

        timestampToString(activity, 18 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 19, dateTimeOf(-3600000L), dateTimeOf(18 * 60 * 60 * 1000))

        timestampToString(activity, 17 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 18, dateTimeOf(-3600000L), dateTimeOf(17 * 60 * 60 * 1000))

        timestampToString(activity, 16 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 17, dateTimeOf(-3600000L), dateTimeOf(16 * 60 * 60 * 1000))

        timestampToString(activity, 15 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 16, dateTimeOf(-3600000L), dateTimeOf(15 * 60 * 60 * 1000))

        timestampToString(activity, 14 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 15, dateTimeOf(-3600000L), dateTimeOf(14 * 60 * 60 * 1000))

        timestampToString(activity, 13 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 14, dateTimeOf(-3600000L), dateTimeOf(13 * 60 * 60 * 1000))

        timestampToString(activity, 12 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 13, dateTimeOf(-3600000L), dateTimeOf(12 * 60 * 60 * 1000))
    }

    @Test
    fun checkTimestampToStringWhenTwentyThreeOrLessHoursAgo() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredBetween12And24HoursAgo(any(), anyInt(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 23 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 23, dateTimeOf(0L), dateTimeOf(23 * 60 * 60 * 1000))

        timestampToString(activity, 22 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 22, dateTimeOf(0L), dateTimeOf(22 * 60 * 60 * 1000))

        timestampToString(activity, 21 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 21, dateTimeOf(0L), dateTimeOf(21 * 60 * 60 * 1000))

        timestampToString(activity, 20 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 20, dateTimeOf(0L), dateTimeOf(20 * 60 * 60 * 1000))

        timestampToString(activity, 19 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 19, dateTimeOf(0L), dateTimeOf(19 * 60 * 60 * 1000))

        timestampToString(activity, 18 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 18, dateTimeOf(0L), dateTimeOf(18 * 60 * 60 * 1000))

        timestampToString(activity, 17 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 17, dateTimeOf(0L), dateTimeOf(17 * 60 * 60 * 1000))

        timestampToString(activity, 16 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 16, dateTimeOf(0L), dateTimeOf(16 * 60 * 60 * 1000))

        timestampToString(activity, 15 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 15, dateTimeOf(0L), dateTimeOf(15 * 60 * 60 * 1000))

        timestampToString(activity, 14 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 14, dateTimeOf(0L), dateTimeOf(14 * 60 * 60 * 1000))

        timestampToString(activity, 13 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween12And24HoursAgo(activity, 13, dateTimeOf(0L), dateTimeOf(13 * 60 * 60 * 1000))
    }

    @Test
    fun checkTimestampToStringWhenOneDayAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredYesterday(any(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 0, formatter, -24 * 60 * 60 * 1000)
        verify(formatter).onEventOccurredYesterday(activity, dateTimeOf(-24 * 60 * 60 * 1000), dateTimeOf(0))
    }

    @Test
    fun checkTimestampToStringWhenOneDayAgo() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredYesterday(any(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredYesterday(activity, dateTimeOf(0L), dateTimeOf(24 * 60 * 60 * 1000))
    }

    @Test
    fun checkTimestampToStringWhenFromOneToSevenDaysAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredBetween2And7DaysAgo(any(), anyInt(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 0, formatter, -2 * 24 * 60 * 60 * 1000)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 2, dateTimeOf(-2 * 24 * 60 * 60 * 1000), dateTimeOf(0L))

        timestampToString(activity, 0, formatter, -3 * 24 * 60 * 60 * 1000)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 3, dateTimeOf(-3 * 24 * 60 * 60 * 1000), dateTimeOf(0L))

        timestampToString(activity, 0, formatter, -4 * 24 * 60 * 60 * 1000)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 4, dateTimeOf(-4 * 24 * 60 * 60 * 1000), dateTimeOf(0L))

        timestampToString(activity, 0, formatter, -5 * 24 * 60 * 60 * 1000)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 5, dateTimeOf(-5 * 24 * 60 * 60 * 1000), dateTimeOf(0L))

        timestampToString(activity, 0, formatter, -6 * 24 * 60 * 60 * 1000)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 6, dateTimeOf(-6 * 24 * 60 * 60 * 1000), dateTimeOf(0L))

        timestampToString(activity, 0, formatter, -7 * 24 * 60 * 60 * 1000)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 7, dateTimeOf(-7 * 24 * 60 * 60 * 1000), dateTimeOf(0L))
    }

    @Test
    fun checkTimestampToStringWhenFromOneToSevenDaysAgo() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredBetween2And7DaysAgo(any(), anyInt(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 2 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 2, dateTimeOf(0), dateTimeOf(2 * 24 * 60 * 60 * 1000))

        timestampToString(activity, 3 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 3, dateTimeOf(0), dateTimeOf(3 * 24 * 60 * 60 * 1000))

        timestampToString(activity, 4 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 4, dateTimeOf(0), dateTimeOf(4 * 24 * 60 * 60 * 1000))

        timestampToString(activity, 5 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 5, dateTimeOf(0), dateTimeOf(5 * 24 * 60 * 60 * 1000))

        timestampToString(activity, 6 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 6, dateTimeOf(0), dateTimeOf(6 * 24 * 60 * 60 * 1000))

        timestampToString(activity, 7 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredBetween2And7DaysAgo(activity, 7, dateTimeOf(0), dateTimeOf(7 * 24 * 60 * 60 * 1000))
    }

    @Test
    fun checkTimestampToStringWhenEightAndMoreDaysAgoAndYearWasChangedInItPeriodButYearsAreSame() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredInSameYear(any(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 0, formatter, -8 * 24 * 60 * 60 * 1000)
        verify(formatter).onEventOccurredInSameYear(activity, dateTimeOf(-8 * 24 * 60 * 60 * 1000), dateTimeOf(0))
    }

    @Test
    fun checkTimestampToStringWhenEightAndMoreDaysAgoButYearsAreSame() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredInSameYear(any(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 8 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).onEventOccurredInSameYear(activity, dateTimeOf(0L), dateTimeOf(8 * 24 * 60 * 60 * 1000))
    }

    @Test
    fun checkTimestampToStringWhenEightAndMoreDaysAgoAndYearWasChangedInItPeriodButYearsAreDifferent() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredInAnotherYear(any(), anyInt(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 0L, formatter, -31536000000L)
        verify(formatter).onEventOccurredInAnotherYear(activity, 1, dateTimeOf(-31536000000L), dateTimeOf(0L))
    }

    @Test
    fun checkTimestampToStringWhenEightAndMoreDaysAgoButYearsAreDifferent() {
        val formatter = mock<TimeFormatter> {
            on { onEventOccurredInAnotherYear(any(), anyInt(), any(), any()) } doReturn ""
        }

        timestampToString(activity, 31536000000L, formatter, 0L)
        verify(formatter).onEventOccurredInAnotherYear(activity, 1, dateTimeOf(0L), dateTimeOf(31536000000L))
    }
}