package ru.sudox.android.core.ui.popup

import android.app.Activity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import ru.sudox.android.core.ui.CommonUiRunner

@RunWith(CommonUiRunner::class)
class CustomPopupMenuBuilderTest {

    @Test
    fun testChecking() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        val menu = CustomPopupMenuBuilder(activity).apply {
            add(0, 1, 0, "First")
            add(0, 2, 0, "Second")
        }

        menu.selectedItemId = 2

        val first = menu.getItem(0)
        val second = menu.getItem(1)

        assertFalse(first.isChecked)
        assertFalse(first.isCheckable)
        assertTrue(second.isChecked)
        assertTrue(second.isCheckable)
        assertEquals(second, menu.selectedItem)
        assertEquals(2, menu.selectedItemId)
    }
}