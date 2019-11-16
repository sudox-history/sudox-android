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

    private var countriesProvider: CountriesProvider? = null
    private var activityController: ActivityController<Activity>? = null
    private var activity: Activity? = null

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
        val valid = linkedMapOf(
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

        assertArrayEquals(valid.keys.toTypedArray(), letters.keys.toTypedArray())
        assertArrayEquals(valid.values.toTypedArray(), letters.values.toTypedArray())
    }

    @Test
    fun testCaching() {
        countriesProvider!!.sortAndCache()

        val countriesBeforeLoading = countriesProvider!!.getLoadedCountries().toTypedArray()
        val lettersBeforeLoading = countriesProvider!!.getLoadedLetters()

        countriesProvider!!.tryLoadOrSort()

        val countriesAfterLoading = countriesProvider!!.getLoadedCountries().toTypedArray()
        val lettersAfterLoading = countriesProvider!!.getLoadedLetters()

        assertArrayEquals(countriesBeforeLoading, countriesAfterLoading)
        assertArrayEquals(lettersBeforeLoading.keys.toTypedArray(), lettersAfterLoading.keys.toTypedArray())
        assertArrayEquals(lettersBeforeLoading.values.toTypedArray(), lettersAfterLoading.values.toTypedArray())
    }
}