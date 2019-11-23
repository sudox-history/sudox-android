package com.sudox.design.navigationBar

import android.app.Activity
import android.os.Bundle
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(DesignTestRunner::class)
class NavigationBarTest : Assert() {

    private var activityController: ActivityController<Activity>? = null
    private var navigationBar: NavigationBar? = null

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
            navigationBar = NavigationBar(this).apply {
                id = Int.MAX_VALUE
            }

            setContentView(navigationBar)
        }

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        activityController!!
                .resume()
                .visible()
    }

    @Test
    fun testCallbacks() = navigationBar!!.let {
        var clickedButtonTag = -1

        it.listener = object : NavigationBarListener {
            override fun onButtonClicked(tag: Int) {
                clickedButtonTag = tag
            }
        }

        it.addItem(1, android.R.string.cut, android.R.drawable.ic_secure)
        it.addItem(2, android.R.string.cancel, android.R.drawable.ic_delete)
        it.addItem(3, android.R.string.selectAll, android.R.drawable.ic_btn_speak_now)
        it.buttons[1].performClick()

        assertEquals(2, clickedButtonTag)
    }

    @Test
    fun testClicking() = navigationBar!!.let {
        it.addItem(1, android.R.string.cut, android.R.drawable.ic_secure)
        it.addItem(2, android.R.string.cancel, android.R.drawable.ic_delete)
        it.addItem(3, android.R.string.selectAll, android.R.drawable.ic_btn_speak_now)

        it.buttons[1].performClick()
        it.buttons[0].performClick()
        it.buttons[2].performClick()

        assertFalse(it.buttons[0].isClicked())
        assertFalse(it.buttons[1].isClicked())
        assertTrue(it.buttons[2].isClicked())
    }

    @Test
    fun testStateSaving() {
        navigationBar!!.addItem(1, android.R.string.cut, android.R.drawable.ic_secure)
        navigationBar!!.addItem(2, android.R.string.cancel, android.R.drawable.ic_delete)
        navigationBar!!.addItem(3, android.R.string.selectAll, android.R.drawable.ic_btn_speak_now)
        navigationBar!!.buttons[1].performClick()
        createActivity()

        val buttons = navigationBar!!.buttons

        assertEquals(3, buttons.size)

        val firstButton = buttons[0]
        val secondButton = buttons[1]
        val thirdButton = buttons[2]

        assertFalse(firstButton.isClicked())
        assertEquals(1, firstButton.tag as Int)
        assertEquals(android.R.string.cut, firstButton.titleId)
        assertEquals(android.R.drawable.ic_secure, firstButton.iconId)

        assertTrue(secondButton.isClicked())
        assertEquals(2, secondButton.tag as Int)
        assertEquals(android.R.string.cancel, secondButton.titleId)
        assertEquals(android.R.drawable.ic_delete, secondButton.iconId)

        assertFalse(thirdButton.isClicked())
        assertEquals(3, thirdButton.tag as Int)
        assertEquals(android.R.string.selectAll, thirdButton.titleId)
        assertEquals(android.R.drawable.ic_btn_speak_now, thirdButton.iconId)
    }
}