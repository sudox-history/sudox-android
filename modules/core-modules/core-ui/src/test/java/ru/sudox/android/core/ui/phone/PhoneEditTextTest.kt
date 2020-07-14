package ru.sudox.android.core.ui.phone

import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import ru.sudox.android.core.ui.CommonUiHiltApplication
import ru.sudox.android.core.ui.CommonUiRunner
import ru.sudox.android.countries.api.CountriesFeatureApi
import javax.inject.Inject

@HiltAndroidTest
@RunWith(CommonUiRunner::class)
@Config(application = CommonUiHiltApplication::class)
class PhoneEditTextTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var featureApi: CountriesFeatureApi

    @Test
    fun checkThatExampleNumberSetAsHint() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val editText = PhoneEditText(controller.get())

        controller.setup()
        editText.setCountry("RU")

        assertEquals("301 123-45-67", editText.phoneEditText.hint)
    }

    @Test
    fun checkThatCountrySelectorShowingValidCodeAndFlag() {
        val validFlag = ShapeDrawable()
        val controller = Robolectric.buildActivity(Activity::class.java)
        val editText = PhoneEditText(controller.get())

        controller.setup()
        hiltRule.inject()

        `when`(featureApi.getCountryFlag("RU")).thenReturn(validFlag)
        editText.setCountry("RU")

        assertEquals(validFlag, editText.countrySelectButton.icon)
        assertEquals("+7", editText.countrySelectButton.text)
    }

    @Test
    fun checkThatReturnedPhoneNumberWithoutSeparatorsAndAdditionalSymbols() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val editText = PhoneEditText(controller.get())

        controller.setup()
        editText.setCountry("RU")
        editText.phoneEditText.setText("9000000000")

        assertEquals("79000000000", editText.getEnteredPhoneNumber())
    }

    @Test
    fun checkThatNumberFormatted() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val editText = PhoneEditText(controller.get())

        controller.setup()
        editText.setCountry("RU")
        editText.phoneEditText.setText("9000000000")

        assertEquals("900 000-00-00", editText.phoneEditText.text.toString())
    }

    @Test
    fun checkThatReturningValidNumberAfterActivityRecreation() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val activity = controller.get()
        val editText = PhoneEditText(activity)

        activity.setContentView(editText)
        controller.setup()

        editText.id = View.generateViewId()
        editText.setCountry("RU")
        editText.phoneEditText.setText("9000000000")

        val bundle = Bundle()
        controller.saveInstanceState(bundle)
        controller.pause()
        controller.start()
        controller.destroy()

        val newController = Robolectric.buildActivity(Activity::class.java)
        val newActivity = newController.get()
        val newEditText = PhoneEditText(newActivity)

        newEditText.id = editText.id
        newActivity.setContentView(newEditText)
        newController.setup(bundle)

        assertEquals("79000000000", newEditText.getEnteredPhoneNumber())
    }

    @AndroidEntryPoint(AppCompatActivity::class)
    class Activity : Hilt_PhoneEditTextTest_Activity()
}