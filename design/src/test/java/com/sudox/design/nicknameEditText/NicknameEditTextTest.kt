package com.sudox.design.nicknameEditText

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
class NicknameEditTextTest : Assert() {

    private var nicknameEditText: NicknameEditText? = null
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
            nicknameEditText = NicknameEditText(this).apply {
                id = Int.MAX_VALUE
            }

            setContentView(nicknameEditText)
        }

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        activityController!!
                .resume()
                .visible()
    }

    @Test
    fun testThatTagInserted() {
        nicknameEditText!!.setTag("4877")
        nicknameEditText!!.setText("nickname")

        assertEquals("nickname#4877", nicknameEditText!!.text.toString())
        assertEquals("nickname", nicknameEditText!!.getNickname())
    }

    @Test
    fun testThatTagRemoved() {
        nicknameEditText!!.setTag("4877")
        nicknameEditText!!.setText("nickname")
        nicknameEditText!!.setText("")

        assertEquals("", nicknameEditText!!.text.toString())
        assertEquals("", nicknameEditText!!.getNickname())
    }

    @Test
    fun testThatTagFixed() {
        nicknameEditText!!.setTag("4877")
        nicknameEditText!!.setText("nickname#4877")
        nicknameEditText!!.setText("nickname#4877-invalid-data")

        assertEquals("nickname#4877", nicknameEditText!!.text.toString())
        assertEquals("nickname", nicknameEditText!!.getNickname())
    }

    @Test
    fun testThatTagFixedBeforeSplitterRemoved() {
        nicknameEditText!!.setTag("4877")
        nicknameEditText!!.setText("nickname#4877")
        nicknameEditText!!.setText("nickname4877")

        assertEquals("nickname#4877", nicknameEditText!!.text.toString())
        assertEquals("nickname", nicknameEditText!!.getNickname())
    }
}