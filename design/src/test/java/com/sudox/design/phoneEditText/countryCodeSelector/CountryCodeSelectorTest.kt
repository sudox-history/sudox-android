package com.sudox.design.phoneEditText.countryCodeSelector

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatEditText
import com.sudox.design.DesignTestRunner
import com.sudox.design.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(DesignTestRunner::class)
class CountryCodeSelectorTest : Assert() {

    private var countryCodeSelector: CountryCodeSelector? = null
    private var activityController: ActivityController<Activity>? = null

    @Before
    fun setUp() {
        createActivity()
    }

    private fun createActivity() {
        var state: Bundle? = null

        activityController?.let {
            state = Bundle()

            it.saveInstanceState(state)
            it.pause()
            it.stop()
            it.destroy()
        }

        activityController = Robolectric
                .buildActivity(Activity::class.java)
                .create()
                .start()

        activityController!!.get().apply {
            countryCodeSelector = CountryCodeSelector(this).apply {
                codePaint = AppCompatEditText(context).paint
                id = Int.MAX_VALUE
            }

            setContentView(countryCodeSelector)
        }

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        activityController!!
                .resume()
                .visible()
    }

    @Test
    fun testCountryChanging() {
        countryCodeSelector!!.set("7", R.drawable.ic_flag_russia)

        assertEquals(R.drawable.ic_flag_russia, countryCodeSelector!!.flagDrawableResId)
        assertEquals("7", countryCodeSelector!!.get())
    }

    @Test
    fun testStateSaving() {
        countryCodeSelector!!.set("7", R.drawable.ic_flag_russia)
        createActivity()

        assertEquals(R.drawable.ic_flag_russia, countryCodeSelector!!.flagDrawableResId)
        assertEquals("7", countryCodeSelector!!.get())
    }
}