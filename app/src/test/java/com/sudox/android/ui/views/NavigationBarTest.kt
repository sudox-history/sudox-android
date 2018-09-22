package com.sudox.android.ui.views

import android.support.v7.widget.AppCompatTextView
import android.view.View
import com.sudox.android.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class NavigationBarTest {

    // Navigation bar mock
    @Mock
    private lateinit var navigationBarMock: NavigationBar
    private var buttonBackMock = mock(AppCompatTextView::class.java)
    private var buttonNextMock = mock(AppCompatTextView::class.java)

    @Before
    fun setUp() {
        doReturn(buttonBackMock)
                .`when`(navigationBarMock)
                .findViewById<AppCompatTextView>(R.id.buttonNavbarBack)

        doReturn(buttonNextMock)
                .`when`(navigationBarMock)
                .findViewById<AppCompatTextView>(R.id.buttonNavbarNext)
    }

    @Test
    fun testBackButton_isnt_visible() {
        navigationBarMock.backButtonIsVisible = false

        // Reconfigure mock
        navigationBarMock.configureComponents()

        // Check, that back button visibility = GONE
        verify(buttonBackMock).visibility = View.GONE
    }

    @Test
    fun testBackButton_is_visible() {
        navigationBarMock.backButtonIsVisible = true

        // Reconfigure mock
        navigationBarMock.configureComponents()

        // Check, that back button visibility = VISIBLE
        verify(buttonBackMock).visibility = View.VISIBLE
    }

    @Test
    fun testBackButton_text() {
        navigationBarMock.backButtonIsVisible = true
        navigationBarMock.backButtonText = "Hello, World!"

        // Reconfigure mock
        navigationBarMock.configureComponents()

        // Check texts equality
        verify(buttonBackMock).text = navigationBarMock.backButtonText
    }

    @Test
    fun testNextButton_isnt_visible() {
        navigationBarMock.nextButtonIsVisible = false

        // Reconfigure mock
        navigationBarMock.configureComponents()

        // Check, that next button visibility = GONE
        verify(buttonNextMock).visibility = View.GONE
    }

    @Test
    fun testNextButton_is_visible() {
        navigationBarMock.nextButtonIsVisible = true

        // Reconfigure mock
        navigationBarMock.configureComponents()

        // Check, that next button visibility = VISIBLE
        verify(buttonNextMock).visibility = View.VISIBLE
    }

    @Test
    fun testNextButton_text() {
        navigationBarMock.nextButtonIsVisible = true
        navigationBarMock.nextButtonText = "Hello, World!"

        // Reconfigure mock
        navigationBarMock.configureComponents()

        // Check texts equality
        verify(buttonNextMock).text = navigationBarMock.nextButtonText
    }
}