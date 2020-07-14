package ru.sudox.phone.watchers

import android.app.Activity
import android.widget.EditText
import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.*

@RunWith(RobolectricTestRunner::class)
class PhoneNumberWatcherTest {

    @Test
    fun testPhoneFormatting() {
        val controller = Robolectric.buildActivity(Activity::class.java)
        val editText = EditText(controller.get())
        val watcher = PhoneTextWatcher(PhoneNumberUtil.getInstance())

        editText.addTextChangedListener(watcher)
        watcher.setCountry("RU", 7)

        controller.get().setContentView(editText)
        controller.create()
        controller.visible()

        editText.setText("3")
        assertEquals("3", editText.text.toString())
        editText.setText("30")
        assertEquals("30", editText.text.toString())
        editText.setText("301")
        assertEquals("301", editText.text.toString())
        editText.setText("3011")
        assertEquals("301 1", editText.text.toString())
        editText.setText("30112")
        assertEquals("301 12", editText.text.toString())
        editText.setText("301123")
        assertEquals("301 123", editText.text.toString())
        editText.setText("3011234")
        assertEquals("301 123-4", editText.text.toString())
        editText.setText("30112345")
        assertEquals("301 123-45", editText.text.toString())
        editText.setText("301123456")
        assertEquals("301 123-45-6", editText.text.toString())
        editText.setText("3011234567")
        assertEquals("301 123-45-67", editText.text.toString())

        editText.setText("301123456")
        assertEquals("301 123-45-6", editText.text.toString())
        editText.setText("30112345")
        assertEquals("301 123-45", editText.text.toString())
        editText.setText("3011234")
        assertEquals("301 123-4", editText.text.toString())
        editText.setText("301123")
        assertEquals("301 123", editText.text.toString())
        editText.setText("30112")
        assertEquals("301 12", editText.text.toString())
        editText.setText("3011")
        assertEquals("301 1", editText.text.toString())
        editText.setText("301")
        assertEquals("301", editText.text.toString())
        editText.setText("30")
        assertEquals("30", editText.text.toString())
        editText.setText("3")
        assertEquals("3", editText.text.toString())
    }
}