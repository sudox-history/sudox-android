package ru.sudox.android.countries.impl

import android.app.Activity
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import ru.sudox.android.countries.impl.shadows.BOOL_TABLE
import ru.sudox.android.countries.impl.shadows.ResourcesMock
import java.util.*

@Config(shadows = [ResourcesMock::class])
@RunWith(RobolectricTestRunner::class)
class CountriesFeatureImplTest {

    @After
    fun tearDown() {
        BOOL_TABLE.clear()
    }

    @Test
    fun testFlagGetting() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val feature = CountriesFeatureImpl(activity.resources, activity.assets, PhoneNumberUtil.getInstance())
        val validDrawable = Drawable.createFromStream(activity.assets.open("flags/1f1f7-1f1fa.png"), null) as BitmapDrawable
        val drawable = feature.getCountryFlag("RU") as BitmapDrawable

        assertTrue(validDrawable.bitmap.sameAs(drawable.bitmap))
        assertEquals(drawable, feature.getCountryFlag("RU"))
    }

    @Test
    @Config(qualifiers = "ru")
    fun testLocaleGetting() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val feature = CountriesFeatureImpl(activity.resources, activity.assets, PhoneNumberUtil.getInstance())

        BOOL_TABLE[R.bool.use_english_countries_names] = true
        assertEquals(Locale.ENGLISH, feature.getCountryNamesLocale())

        BOOL_TABLE[R.bool.use_english_countries_names] = false
        assertEquals(Locale("ru"), feature.getCountryNamesLocale())
    }

    @Test
    @Config(qualifiers = "ru")
    fun testNameGetting() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val feature = CountriesFeatureImpl(activity.resources, activity.assets, PhoneNumberUtil.getInstance())

        BOOL_TABLE[R.bool.use_english_countries_names] = true
        assertEquals("Russia", feature.getCountryName("RU"))

        BOOL_TABLE[R.bool.use_english_countries_names] = false
        assertEquals("Россия", feature.getCountryName("RU"))
    }

    @Test
    fun testCodeGetting() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val feature = CountriesFeatureImpl(activity.resources, activity.assets, PhoneNumberUtil.getInstance())

        assertEquals(7, feature.getCountryCode("RU"))
    }
}