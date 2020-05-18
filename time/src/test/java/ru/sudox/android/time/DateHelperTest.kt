package ru.sudox.android.time

import android.app.Activity
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.systemContext
import org.robolectric.annotation.Config
import org.junit.Assert.*
import org.robolectric.Robolectric
import java.util.TimeZone

@Config(qualifiers = "en")
@RunWith(RobolectricTestRunner::class)
class DateHelperTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()

    init {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    }

    @Test
    fun checkTimestampToDateStringFormattingWithFullMonthNamesAndSameYears() {
        assertEquals("January 1", timestampToDateString(activity, 0, true, 0))
    }

    @Test
    fun checkTimestampToDateStringFormattingWithFullMonthNamesAndDifferentYears() {
        assertEquals("January 1, 1970", timestampToDateString(activity, 0, true, 31536000000))
    }

    @Test
    fun checkTimestampToDateStringFormattingWithShortMonthNamesAndSameYears() {
        assertEquals("jan 1", timestampToDateString(activity, 0, false, 0))
    }

    @Test
    fun checkTimestampToDateStringFormattingWithShortMonthNamesAndDifferentYears() {
        assertEquals("jan 1, 1970", timestampToDateString(activity, 0, false, 31536000000))
    }
}