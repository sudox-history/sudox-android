package com.sudox.design.phoneEditText

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.autofill.AutofillValue
import com.sudox.design.DesignTestRunner
import com.sudox.design.R
import com.sudox.design.phoneNumberUtil
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(DesignTestRunner::class)
class PhoneEditTextTest : Assert() {

    private var phoneEditText: PhoneEditText? = null
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
            phoneEditText = PhoneEditText(this).apply {
                id = Int.MAX_VALUE
            }

            phoneNumberUtil = PhoneNumberUtil.createInstance(this)
            setContentView(phoneEditText)
        }

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        activityController!!
                .resume()
                .visible()
    }

    @Test
    fun checkThatHintInstalled() {
        phoneEditText!!.setCountry("RU", 7, R.drawable.ic_flag_russia)
        assertEquals("301 123-45-67", phoneEditText!!.numberEditText.hint.toString())
    }

    @Test
    fun checkThatUserCanGetPhoneNumberAndRegion() {
        phoneEditText!!.setCountry("RU", 7, R.drawable.ic_flag_russia)
        phoneEditText!!.numberEditText.setText("9000000000")

        assertEquals("79000000000", phoneEditText!!.getPhoneNumber())
        assertEquals("RU", phoneEditText!!.getRegionCode())
    }

    @Test
    fun testCountryChangingWithResetting() {
        phoneEditText!!.setCountry("RU", 7, R.drawable.ic_flag_russia)
        phoneEditText!!.numberEditText.setText("9000000000")
        phoneEditText!!.setCountry("UA", 380, R.drawable.ic_flag_undefined, true)

        assertNull(phoneEditText!!.getPhoneNumber())
        assertEquals("UA", phoneEditText!!.getRegionCode())
    }

    @Test
    fun testCountryChangingWithoutResetting() {
        phoneEditText!!.setCountry("RU", 7, R.drawable.ic_flag_russia)
        phoneEditText!!.numberEditText.setText("9000000000")
        phoneEditText!!.setCountry("UA", 380, R.drawable.ic_flag_undefined, false)

        assertEquals("3809000000000", phoneEditText!!.getPhoneNumber())
        assertEquals("UA", phoneEditText!!.getRegionCode())
    }

    @Test
    fun testWhenNumberEnteredButCountryNotDefined() {
        phoneEditText!!.numberEditText.setText("9000000000")

        assertNull(phoneEditText!!.getPhoneNumber())
        assertNull(phoneEditText!!.getRegionCode())
    }

    @Test
    fun testSituationWhenRegionNotDefined() {
        assertNull(phoneEditText!!.getPhoneNumber())
        assertNull(phoneEditText!!.getRegionCode())
    }

    @Test
    fun testFormatting() {
        phoneEditText!!.setCountry("RU", 7, R.drawable.ic_flag_russia)
        phoneEditText!!.numberEditText.setText("9000000000")

        assertEquals("900 000-00-00", phoneEditText!!.numberEditText.text.toString())
    }

    @Test
    fun testStateSaving() {
        phoneEditText!!.setCountry("RU", 7, R.drawable.ic_flag_russia)
        phoneEditText!!.numberEditText.setText("9000000000")
        createActivity()

        assertEquals("RU", phoneEditText!!.getRegionCode())
        assertEquals("79000000000", phoneEditText!!.getPhoneNumber())
        assertEquals("900 000-00-00", phoneEditText!!.numberEditText.text.toString())
        assertEquals("301 123-45-67", phoneEditText!!.numberEditText.hint.toString())
        assertEquals(R.drawable.ic_flag_russia, phoneEditText!!.countryCodeSelector.flagDrawableResId)
        assertEquals(7, phoneEditText!!.countryCodeSelector.get())
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun testAutofillWithoutCountryChanging() {
        val value = AutofillValue.forText("+79000000000")

        phoneEditText!!.setCountry("RU", 7, R.drawable.ic_flag_russia)
        phoneEditText!!.numberEditText.autofill(value)

        assertEquals("RU", phoneEditText!!.getRegionCode())
        assertEquals("79000000000", phoneEditText!!.getPhoneNumber())
        assertEquals("900 000-00-00", phoneEditText!!.numberEditText.text.toString())
        assertEquals(7, phoneEditText!!.countryCodeSelector.get())
        assertEquals(13, phoneEditText!!.numberEditText.selectionEnd)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun testAutofillWithCountryChanging() {
        val value = AutofillValue.forText("+380000000000")

        phoneEditText!!.regionFlagIdCallback = { R.drawable.ic_flag_undefined }
        phoneEditText!!.setCountry("RU", 7, R.drawable.ic_flag_russia)
        phoneEditText!!.numberEditText.autofill(value)

        assertEquals("UA", phoneEditText!!.getRegionCode())
        assertEquals("380000000000", phoneEditText!!.getPhoneNumber())
        assertEquals("00 0000000", phoneEditText!!.numberEditText.text.toString())
        assertEquals(380, phoneEditText!!.countryCodeSelector.get())
        assertEquals(10, phoneEditText!!.numberEditText.selectionEnd)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O])
    fun testWhenAutofillNotAllowing() {
        val value = AutofillValue.forText("+380000000000")

        phoneEditText!!.regionFlagIdCallback = { 0 }
        phoneEditText!!.setCountry("RU", 7, R.drawable.ic_flag_russia)
        phoneEditText!!.numberEditText.setText("9000000000")
        phoneEditText!!.numberEditText.autofill(value)
        phoneEditText!!.numberEditText.setSelection(5)

        assertEquals("RU", phoneEditText!!.getRegionCode())
        assertEquals("79000000000", phoneEditText!!.getPhoneNumber())
        assertEquals("900 000-00-00", phoneEditText!!.numberEditText.text.toString())
        assertEquals(7, phoneEditText!!.countryCodeSelector.get())
        assertEquals(5, phoneEditText!!.numberEditText.selectionEnd)
    }
}