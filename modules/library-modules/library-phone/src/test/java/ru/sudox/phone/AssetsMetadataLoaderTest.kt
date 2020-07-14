package ru.sudox.phone

import android.app.Activity
import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.*

@RunWith(RobolectricTestRunner::class)
class AssetsMetadataLoaderTest {

    @Test
    fun testLoading() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val phoneNumberUtil = PhoneNumberUtil.createInstance(AssetsMetadataLoader(activity.assets))
        val validExampleNumber = PhoneNumberUtil.getInstance().getExampleNumber("RU")
        val exampleNumber = phoneNumberUtil.getExampleNumber("RU")

        assertEquals(validExampleNumber, exampleNumber)
    }
}