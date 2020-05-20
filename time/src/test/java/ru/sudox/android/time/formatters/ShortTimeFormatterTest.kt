package ru.sudox.android.time.formatters

import android.app.Activity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.sudox.android.time.dateTimeOf
import ru.sudox.android.time.formatters.ShortTimeFormatter.onEventOccurredBetween12And24HoursAgo
import ru.sudox.android.time.formatters.ShortTimeFormatter.onEventOccurredBetween1And12HoursAgo
import ru.sudox.android.time.formatters.ShortTimeFormatter.onEventOccurredBetween1And60MinutesAgo
import ru.sudox.android.time.formatters.ShortTimeFormatter.onEventOccurredBetween2And7DaysAgo
import ru.sudox.android.time.formatters.ShortTimeFormatter.onEventOccurredInAnotherYear
import ru.sudox.android.time.formatters.ShortTimeFormatter.onEventOccurredInSameYear
import ru.sudox.android.time.formatters.ShortTimeFormatter.onEventOccurredNow
import ru.sudox.android.time.formatters.ShortTimeFormatter.onEventOccurredYesterday
import java.util.TimeZone

@Config(qualifiers = "en")
@RunWith(RobolectricTestRunner::class)
class ShortTimeFormatterTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()

    init {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    }

    @Test
    fun testWhenEventOccurredNow() {
        assertEquals("now", onEventOccurredNow(activity, dateTimeOf(0L), dateTimeOf(0L), false))
    }

    @Test
    fun testWhenEventOccurredBetween1And60MinutesAgo() {
        assertEquals("1m", onEventOccurredBetween1And60MinutesAgo(activity, 1, dateTimeOf(0L), dateTimeOf(0L), false))
    }

    @Test
    fun testWhenEventOccurredBetween1And12HoursAgo() {
        assertEquals("1h", onEventOccurredBetween1And12HoursAgo(activity, 1, dateTimeOf(0L), dateTimeOf(0L), false))
    }

    @Test
    fun testWhenEventOccurredBetween12And24HoursAgo() {
        assertEquals("13h", onEventOccurredBetween12And24HoursAgo(activity, 13, dateTimeOf(0L), dateTimeOf(0L), false))
    }

    @Test
    fun testWhenEventOccurredYesterday() {
        assertEquals("yesterday", onEventOccurredYesterday(activity, dateTimeOf(0L), dateTimeOf(0L), false))
    }

    @Test
    fun testWhenEventOccurredBetween2And7DaysAgo() {
        assertEquals("thu", onEventOccurredBetween2And7DaysAgo(activity, 2, dateTimeOf(0L), dateTimeOf(0L), false))
    }

    @Test
    fun testWhenEventOccurredInSameYear() {
        assertEquals("jan 1", onEventOccurredInSameYear(activity, dateTimeOf(0L), dateTimeOf(0L), false))
    }

    @Test
    fun testWhenEventOccurredInAnotherYear() {
        assertEquals("1y", onEventOccurredInAnotherYear(activity, 1, dateTimeOf(-31536000L), dateTimeOf(0L), false))
    }
}