package com.sudox.design.countriesProvider

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(DesignTestRunner::class)
class CountriesProviderTest : Assert() {

    private var activityController: ActivityController<Activity>? = null
    private var activity: Activity? = null
    private var countriesProvider: CountriesProvider? = null

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController!!.get()
        countriesProvider = CountriesProvider(activity!!)
    }

    @Test
    fun testSorting() {
        val valid = arrayOf(
                "Czech Republic",
                "Germany",
                "Hungary",
                "Iceland",
                "Italy",
                "Latvia",
                "Lithuania",
                "Neatherlands",
                "Poland",
                "Russia",
                "Sweden",
                "Ukraine"
        )

        assertArrayEquals(valid, with(countriesProvider!!) {
            sortAndCache()

            getLoadedCountries().map {
                it.getName(context)
            }.toTypedArray()
        })
    }

    @Test
    fun testLettersGrouping() {
        val valid = hashMapOf(
                11 to "U",
                10 to "S",
                9 to "R",
                8 to "P",
                7 to "N",
                5 to "L",
                3 to "I",
                2 to "H",
                1 to "G",
                0 to "C"
        )

        val letters = with(countriesProvider!!) {
            sortAndCache()
            tryLoadOrSort()
            getLoadedLetters()
        }

        assertTrue(valid.entries == letters.entries)
    }

    @Test
    fun testCaching() {
        val countries = with(countriesProvider!!) {
            sortAndCache()
            tryLoadOrSort()
            getLoadedCountries()
        }

        assertNotNull(countries)
        assertEquals(countries.size, countries.size)
    }

    @Test
    fun testVersioning() {
        countriesProvider!!.sortAndCache()

        activity!!.getSharedPreferences(PREFS_COUNTRIES, Context.MODE_PRIVATE).edit {
            remove(PREF_APP_VERSION_CODE)
            putInt(PREF_APP_VERSION_CODE, Integer.MAX_VALUE)
        }

        val countries = with(countriesProvider!!) {
            tryLoadOrSort()
            getLoadedCountries()
        }

        assertNotNull(countries)
        assertEquals(countries.size, countries.size)
    }

    @Test
    fun testLanguageChanging() {
        countriesProvider!!.sortAndCache()

        activity!!.getSharedPreferences(PREFS_COUNTRIES, Context.MODE_PRIVATE).edit {
            remove(PREF_APP_LANGUAGE)
            putString(PREF_APP_LANGUAGE, "Russian")
        }

        val countries = with(countriesProvider!!) {
            tryLoadOrSort()
            getLoadedCountries()
        }

        assertNotNull(countries)
        assertEquals(countries.size, countries.size)
    }
}