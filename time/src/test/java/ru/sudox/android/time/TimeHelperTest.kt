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
import org.mockito.ArgumentMatchers.anyLong
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
        assertEquals("00:00", timestampToTimeString(activity, 0L, false))
        assertEquals("20:00", timestampToTimeString(activity, 72000000, false))
        assertEquals("00:20", timestampToTimeString(activity, 1815600000, false))
    }

    @Test
    fun checkTimestampToTimeStringIn12HoursFormat() {
        assertEquals("00:00 AM", timestampToTimeString(activity, 0L, true))
        assertEquals("08:00 PM", timestampToTimeString(activity, 72000000, true))
        assertEquals("00:20 AM", timestampToTimeString(activity, 1815600000, true))
    }

    @Test
    fun checkTimestampToStringWhenSecondsAgo() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredNow(any()) } doReturn ""
        }

        timestampToString(activity, 5000L, formatter, 0L)
        verify(formatter).formatWhenEventOccurredNow(activity)
    }

    @Test
    fun checkTimestampToStringWhenSecondsAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredNow(any()) } doReturn ""
        }

        timestampToString(activity, 5000L, formatter, -1000L)
        verify(formatter).formatWhenEventOccurredNow(activity)
    }

    @Test
    fun checkTimestampToStringWhenMinutesAgo() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredBetween1And60MinutesAgo(any(), anyInt(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 600000L, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And60MinutesAgo(activity, 10, 0L)
    }

    @Test
    fun checkTimestampToStringWhenMinutesAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredBetween1And60MinutesAgo(any(), anyInt(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 540000L, formatter, -60000L)
        verify(formatter).formatWhenEventOccurredBetween1And60MinutesAgo(activity, 10, -60000L)
    }

    @Test
    fun checkTimestampToStringWhenTwelveOrLessHoursAgo() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredBetween1And12HoursAgo(any(), anyInt(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 12 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 12, 0L)

        timestampToString(activity, 11 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 11, 0L)

        timestampToString(activity, 10 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 10, 0L)

        timestampToString(activity, 9 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 9, 0L)

        timestampToString(activity, 8 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 8, 0L)

        timestampToString(activity, 7 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 7, 0L)

        timestampToString(activity, 6 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 6, 0L)

        timestampToString(activity, 5 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 5, 0L)

        timestampToString(activity, 4 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 4, 0L)

        timestampToString(activity, 3 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 3, 0L)

        timestampToString(activity, 2 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 2, 0L)

        timestampToString(activity, 1 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 1, 0L)
    }

    @Test
    fun checkTimestampToStringWhenTwelveOrLessHoursAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredBetween1And12HoursAgo(any(), anyInt(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 11 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 12, -3600000L)

        timestampToString(activity, 10 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 11, -3600000L)

        timestampToString(activity, 9 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 10, -3600000L)

        timestampToString(activity, 8 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 9, -3600000L)

        timestampToString(activity, 7 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 8, -3600000L)

        timestampToString(activity, 6 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 7, -3600000L)

        timestampToString(activity, 5 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 6, -3600000L)

        timestampToString(activity, 4 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 5, -3600000L)

        timestampToString(activity, 3 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 4, -3600000L)

        timestampToString(activity, 2 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 3, -3600000L)

        timestampToString(activity, 1 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 2, -3600000L)

        timestampToString(activity, 0L, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween1And12HoursAgo(activity, 1, -3600000L)
    }

    @Test
    fun checkTimestampToStringWhenTwentyThreeOrLessHoursAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredBetween12And24HoursAgo(any(), anyInt(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 22 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 23, -3600000L)

        timestampToString(activity, 21 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 22, -3600000L)

        timestampToString(activity, 20 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 21, -3600000L)

        timestampToString(activity, 19 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 20, -3600000L)

        timestampToString(activity, 18 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 19, -3600000L)

        timestampToString(activity, 17 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 18, -3600000L)

        timestampToString(activity, 16 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 17, -3600000L)

        timestampToString(activity, 15 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 16, -3600000L)

        timestampToString(activity, 14 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 15, -3600000L)

        timestampToString(activity, 13 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 14, -3600000L)

        timestampToString(activity, 12 * 60 * 60 * 1000, formatter, -3600000L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 13, -3600000L)
    }

    @Test
    fun checkTimestampToStringWhenTwentyThreeOrLessHoursAgo() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredBetween12And24HoursAgo(any(), anyInt(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 23 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 23, 0L)

        timestampToString(activity, 22 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 22, 0L)

        timestampToString(activity, 21 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 21, 0L)

        timestampToString(activity, 20 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 20, 0L)

        timestampToString(activity, 19 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 19, 0L)

        timestampToString(activity, 18 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 18, 0L)

        timestampToString(activity, 17 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 17, 0L)

        timestampToString(activity, 16 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 16, 0L)

        timestampToString(activity, 15 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 15, 0L)

        timestampToString(activity, 14 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 14, 0L)

        timestampToString(activity, 13 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween12And24HoursAgo(activity, 13, 0L)
    }

    @Test
    fun checkTimestampToStringWhenOneDayAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredYesterday(any(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 0, formatter, -24 * 60 * 60 * 1000)
        verify(formatter).formatWhenEventOccurredYesterday(activity, -24 * 60 * 60 * 1000)
    }

    @Test
    fun checkTimestampToStringWhenOneDayAgo() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredYesterday(any(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredYesterday(activity, 0L)
    }

    @Test
    fun checkTimestampToStringWhenFromOneToSevenDaysAgoAndYearWasChangedInItPeriod() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredBetween2And7DaysAgo(any(), anyInt(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 0, formatter, -2 * 24 * 60 * 60 * 1000)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 2, -2 * 24 * 60 * 60 * 1000)

        timestampToString(activity, 0, formatter, -3 * 24 * 60 * 60 * 1000)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 3, -3 * 24 * 60 * 60 * 1000)

        timestampToString(activity, 0, formatter, -4 * 24 * 60 * 60 * 1000)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 4, -4 * 24 * 60 * 60 * 1000)

        timestampToString(activity, 0, formatter, -5 * 24 * 60 * 60 * 1000)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 5, -5 * 24 * 60 * 60 * 1000)

        timestampToString(activity, 0, formatter, -6 * 24 * 60 * 60 * 1000)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 6, -6 * 24 * 60 * 60 * 1000)

        timestampToString(activity, 0, formatter, -7 * 24 * 60 * 60 * 1000)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 7, -7 * 24 * 60 * 60 * 1000)
    }

    @Test
    fun checkTimestampToStringWhenFromOneToSevenDaysAgo() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredBetween2And7DaysAgo(any(), anyInt(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 2 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 2, 0L)

        timestampToString(activity, 3 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 3, 0L)

        timestampToString(activity, 4 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 4, 0L)

        timestampToString(activity, 5 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 5, 0L)

        timestampToString(activity, 6 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 6, 0L)

        timestampToString(activity, 7 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredBetween2And7DaysAgo(activity, 7, 0L)
    }

    @Test
    fun checkTimestampToStringWhenEightAndMoreDaysAgoAndYearWasChangedInItPeriodButYearsAreSame() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredInSameYear(any(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 0, formatter, -8 * 24 * 60 * 60 * 1000)
        verify(formatter).formatWhenEventOccurredInSameYear(activity, -8 * 24 * 60 * 60 * 1000)
    }

    @Test
    fun checkTimestampToStringWhenEightAndMoreDaysAgoButYearsAreSame() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredInSameYear(any(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 8 * 24 * 60 * 60 * 1000, formatter, 0L)
        verify(formatter).formatWhenEventOccurredInSameYear(activity, 0L)
    }

    @Test
    fun checkTimestampToStringWhenEightAndMoreDaysAgoAndYearWasChangedInItPeriodButYearsAreDifferent() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredInAnotherYear(any(), anyInt(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 0L, formatter, -31536000000L)
        verify(formatter).formatWhenEventOccurredInAnotherYear(activity, 1, -31536000000L)
    }

    @Test
    fun checkTimestampToStringWhenEightAndMoreDaysAgoButYearsAreDifferent() {
        val formatter = mock<TimeFormatter> {
            on { formatWhenEventOccurredInAnotherYear(any(), anyInt(), anyLong()) } doReturn ""
        }

        timestampToString(activity, 31536000000L, formatter, 0L)
        verify(formatter).formatWhenEventOccurredInAnotherYear(activity, 1, 0L)
    }
}