package com.sudox.design.codeEditText

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(DesignTestRunner::class)
class CodeEditTextTest : Assert() {

    private var codeEditText: CodeEditText? = null
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
            codeEditText = CodeEditText(this).apply {
                id = Int.MAX_VALUE
            }

            setContentView(codeEditText)
        }

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        activityController!!
                .resume()
                .visible()
    }

    @Test
    fun checkThatNextBlockFocusedAfterDigitEntering() {
        for (i in 0 until codeEditText!!.digitsEditTexts!!.size - 1) {
            codeEditText!!.digitsEditTexts!![i].apply {
                printOne()

                assertEquals("1", text.toString())
                assertTrue(codeEditText!!.digitsEditTexts!![i + 1].isFocused)
            }
        }
    }

    @Test
    fun checkThatCodeReturnedAfterCodePrinting() {
        var codeFromCallback: String? = null
        val validCode = "1".repeat(codeEditText!!.digitsEditTexts!!.size)

        codeEditText!!.codeFilledCallback = { codeFromCallback = it }
        codeEditText!!.digitsEditTexts!!.forEach {
            it.printOne()
        }

        assertEquals(validCode, codeFromCallback)
        assertEquals(validCode, codeEditText!!.getCode())
        assertTrue(codeEditText!!.digitsEditTexts!!.last().isFocused)
    }

    @Test
    fun checkThatCodeNotReturnedWhenItNotFullyPrinted() {
        var codeFromCallback: String? = null
        codeEditText!!.codeFilledCallback = { codeFromCallback = it }

        for (i in 0 until codeEditText!!.digitsEditTexts!!.size - 1) {
            codeEditText!!.digitsEditTexts!![i].apply {
                printOne()

                assertNull(codeFromCallback)
                assertNull(codeEditText!!.getCode())
            }
        }
    }

    @Test
    fun checkThatCallbackNotCalledWhenCodeNotFullyPrinted() {
        var codeFromCallback: String? = null
        codeEditText!!.codeFilledCallback = { codeFromCallback = it }

        for (i in 0 until codeEditText!!.digitsEditTexts!!.size - 1) {
            codeEditText!!.digitsEditTexts!![i].apply {
                printOne()
                dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                assertNull(codeFromCallback)
            }
        }
    }

    @Test
    fun checkThatPreviousBlockFocusedAfterDeleteButtonClicked() {
        codeEditText!!.digitsEditTexts!!.forEach {
            it.printOne()
        }

        for (i in codeEditText!!.digitsEditTexts!!.size - 1 downTo 1) {
            codeEditText!!.digitsEditTexts!![i].apply {
                deleteDigit()

                assertTrue(codeEditText!!.digitsEditTexts!![i - 1].isFocused)
                assertTrue(text.isNullOrEmpty())
            }
        }
    }

    @Test
    fun testDigitReplacingWhenCursorOnStart() {
        codeEditText!!.digitsEditTexts!!.forEach {
            it.printOne()
        }

        codeEditText!!.digitsEditTexts!!.forEachIndexed { i, it ->
            it.setSelection(0)
            it.printTwo()

            assertEquals("2", it.text.toString())

            if (i < codeEditText!!.digitsEditTexts!!.lastIndex) {
                assertTrue(codeEditText!!.digitsEditTexts!![i + 1].isFocused)
            }
        }
    }

    @Test
    fun testDigitReplacingWhenCursorOnEnd() {
        codeEditText!!.digitsEditTexts!!.forEach {
            it.printOne()
        }

        codeEditText!!.digitsEditTexts!!.forEachIndexed { i, it ->
            it.setSelection(1)
            it.printTwo()

            assertEquals("2", it.text.toString())

            if (i < codeEditText!!.digitsEditTexts!!.lastIndex) {
                assertTrue(codeEditText!!.digitsEditTexts!![i + 1].isFocused)
            }
        }
    }

    @Test
    fun checkThatInvalidSymbolsNotAllowed() {
        codeEditText!!.digitsEditTexts!!.forEach {
            it.printA()

            assertTrue(it.text.isNullOrEmpty())
            assertTrue(it.isFocused)
        }
    }

    @Test
    fun checkThatStringsNotPasted() {
        codeEditText!!.digitsEditTexts!!.forEach {
            it.requestFocus()
            it.setText("ABCD")

            assertTrue(it.text.isNullOrEmpty())
            assertTrue(it.isFocused)
        }
    }

    @Test
    fun testStateSaving() {
        codeEditText!!.digitsEditTexts!!.forEach { it.printOne() }
        codeEditText!!.digitsEditTexts!![2].requestFocus()
        codeEditText!!.digitsEditTexts!![2].setSelection(0)
        createActivity()

        assertEquals(0, codeEditText!!.digitsEditTexts!![2].selectionStart)
        assertEquals(0, codeEditText!!.digitsEditTexts!![2].selectionEnd)
        assertTrue(codeEditText!!.digitsEditTexts!![2].isFocused)

        codeEditText!!.digitsEditTexts!!.forEach {
            assertEquals("1", it.text.toString())
        }
    }

    private fun EditText.deleteDigit() {
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
        text = null
    }

    private fun EditText.printOne() {
        requestFocus()
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_1))

        if (selectionStart == 1) {
            setText("${text}1")
        } else {
            setText("1$text")
        }
    }

    private fun EditText.printA() {
        requestFocus()
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A))

        if (selectionStart == 1) {
            setText("${text}A")
        } else {
            setText("A$text")
        }
    }

    private fun EditText.printTwo() {
        requestFocus()
        dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_2))

        if (selectionStart == 1) {
            setText("${text}2")
        } else {
            setText("2$text")
        }
    }
}