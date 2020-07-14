package ru.sudox.android.core.ui.scrollview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import ru.sudox.android.core.ui.CommonUiRunner

@RunWith(CommonUiRunner::class)
class RestorableScrollViewTest {

    @Test
    fun testThatCallbackInvoked() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java)
        val activity = controller.get()
        val scrollView = RestorableScrollView(activity)
        val bundle = Bundle()

        scrollView.id = View.generateViewId()
        activity.setContentView(scrollView)
        controller.setup()
        controller.saveInstanceState(bundle)
        controller.pause()
        controller.stop()
        controller.destroy()

        val newController = Robolectric.buildActivity(AppCompatActivity::class.java)
        val newActivity = newController.get()
        val newScrollView = RestorableScrollView(newActivity)
        var invokesCount = 0

        newScrollView.id = scrollView.id
        newScrollView.restoreStateEventCallback = { invokesCount++ }
        newActivity.setContentView(newScrollView)
        newController.setup(bundle)

        assertEquals(1, invokesCount)
    }
}