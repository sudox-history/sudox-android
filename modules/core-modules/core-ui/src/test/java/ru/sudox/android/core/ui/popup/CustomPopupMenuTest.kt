package ru.sudox.android.core.ui.popup

import android.app.Activity
import android.os.Build
import android.transition.Transition
import android.view.MenuItem
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.widget.ListPopupWindow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import ru.sudox.android.core.ui.CommonUiRunner
import ru.sudox.android.core.ui.R
import ru.sudox.android.core.ui.popup.shadows.TRANSITIONS_MAPPINGS
import ru.sudox.android.core.ui.popup.shadows.TransitionInflaterShadow

@RunWith(CommonUiRunner::class)
class CustomPopupMenuTest {

    @After
    fun tearDown() {
        TRANSITIONS_MAPPINGS.remove(R.transition.transition_popup_enter)
        TRANSITIONS_MAPPINGS.remove(R.transition.transition_popup_exit)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.M], shadows = [TransitionInflaterShadow::class])
    fun checkThatTransitionsChangedOnMarshmallow() {
        val validEnterTransition = Mockito.mock(Transition::class.java)
        val validExitTransition = Mockito.mock(Transition::class.java)

        TRANSITIONS_MAPPINGS[R.transition.transition_popup_enter] = validEnterTransition
        TRANSITIONS_MAPPINGS[R.transition.transition_popup_exit] = validExitTransition

        val controller = Robolectric.buildActivity(Activity::class.java).apply {
            create()
            visible()
        }

        val menu = CustomPopupMenu(controller.get())
        val popup = ListPopupWindow::class.java
            .getDeclaredField("mPopup")
            .apply { isAccessible = true }
            .get(menu) as PopupWindow

        // getters not exists in Android M
        val enterTransition = PopupWindow::class.java
            .getDeclaredField("mEnterTransition")
            .apply { isAccessible = true }
            .get(popup) as Transition

        val exitTransaction = PopupWindow::class.java
            .getDeclaredField("mExitTransition")
            .apply { isAccessible = true }
            .get(popup) as Transition

        assertEquals(validEnterTransition, enterTransition)
        assertEquals(validExitTransition, exitTransaction)
    }

    @Test
    fun checkThatCallbackInvokedAndSelectedItemChanged() {
        val controller = Robolectric.buildActivity(Activity::class.java).apply {
            create()
            visible()
        }

        var clicked: MenuItem? = null
        val activity = controller.get()
        val window = CustomPopupMenu(controller.get()).apply {
            anchorView = View(activity)
            clickCallback = { clicked = it }
            menu = CustomPopupMenuBuilder(activity).also {
                it.add(0, 1, 0, "First")
                it.add(0, 2, 1, "Second")
                it.selectedItemId = 1
            }
        }

        window.show()
        window.performItemClick(1)

        assertEquals(2, window.menu!!.selectedItemId)
        assertEquals(2, clicked!!.itemId)
    }
}