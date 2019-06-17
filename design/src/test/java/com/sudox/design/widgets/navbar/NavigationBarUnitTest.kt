package com.sudox.design.widgets.navbar

import android.view.View
import com.sudox.design.widgets.navbar.button.NavigationBarButton
import com.sudox.design.widgets.navbar.button.NavigationBarButtonParams
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(NavigationBar::class, NavigationBarButton::class)
class NavigationBarUnitTest : Assert() {

    private lateinit var navigationBar: NavigationBar
    private lateinit var buttonStart: NavigationBarButton

    @Before
    fun setUp() {
        navigationBar = PowerMockito.mock(NavigationBar::class.java)
        buttonStart = PowerMockito.mock(NavigationBarButton::class.java)

        NavigationBar::class.java
                .getDeclaredField("buttonStart")
                .apply { isAccessible = true }
                .set(navigationBar, buttonStart)
    }

    @Test
    fun testLayoutEnd_ltr_one_button() {
        val button = PowerMockito.mock(NavigationBarButton::class.java)

        NavigationBar::class.java
                .getDeclaredField("buttonsEnd")
                .apply { isAccessible = true }
                .set(navigationBar, arrayOf(button))

        Mockito.`when`(button.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(button.measuredWidth).thenReturn(50)
        Mockito.`when`(navigationBar.getEndLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(140)
        Mockito.`when`(navigationBar.layoutEnd(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        navigationBar.layoutEnd(0, 150, 0, 60, false)
        Mockito.verify(button).layout(90, 0, 140, 60)
    }

    @Test
    fun testLayoutEnd_rtl_one_button() {
        val button = PowerMockito.mock(NavigationBarButton::class.java)

        NavigationBar::class.java
                .getDeclaredField("buttonsEnd")
                .apply { isAccessible = true }
                .set(navigationBar, arrayOf(button))

        Mockito.`when`(button.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(button.measuredWidth).thenReturn(50)
        Mockito.`when`(navigationBar.getEndLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(10)
        Mockito.`when`(navigationBar.layoutEnd(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        navigationBar.layoutEnd(0, 150, 0, 60, true)
        Mockito.verify(button).layout(10, 0, 60, 60)
    }

    @Test
    fun testLayoutEnd_ltr_many_buttons() {
        val firstButton = PowerMockito.mock(NavigationBarButton::class.java)
        val secondButton = PowerMockito.mock(NavigationBarButton::class.java)
        val thirdButton = PowerMockito.mock(NavigationBarButton::class.java)
        val buttons = arrayOf(firstButton, secondButton, thirdButton)

        NavigationBar::class.java
                .getDeclaredField("buttonsEnd")
                .apply { isAccessible = true }
                .set(navigationBar, buttons)

        for (button in buttons) {
            Mockito.`when`(button.visibility).thenReturn(View.VISIBLE)
            Mockito.`when`(button.measuredWidth).thenReturn(50)
        }

        Mockito.`when`(navigationBar.getEndLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(290)
        Mockito.`when`(navigationBar.layoutEnd(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        navigationBar.layoutEnd(0, 300, 0, 60, false)
        Mockito.verify(thirdButton).layout(240, 0, 290, 60)
        Mockito.verify(secondButton).layout(190, 0, 240, 60)
        Mockito.verify(firstButton).layout(140, 0, 190, 60)
    }

    @Test
    fun testLayoutEnd_rtl_many_buttons() {
        val firstButton = PowerMockito.mock(NavigationBarButton::class.java)
        val secondButton = PowerMockito.mock(NavigationBarButton::class.java)
        val thirdButton = PowerMockito.mock(NavigationBarButton::class.java)
        val buttons = arrayOf(firstButton, secondButton, thirdButton)

        NavigationBar::class.java
                .getDeclaredField("buttonsEnd")
                .apply { isAccessible = true }
                .set(navigationBar, buttons)

        for (button in buttons) {
            Mockito.`when`(button.visibility).thenReturn(View.VISIBLE)
            Mockito.`when`(button.measuredWidth).thenReturn(50)
        }

        Mockito.`when`(navigationBar.getEndLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(10)
        Mockito.`when`(navigationBar.layoutEnd(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        navigationBar.layoutEnd(0, 300, 0, 60, true)
        Mockito.verify(thirdButton).layout(10, 0, 60, 60)
        Mockito.verify(secondButton).layout(60, 0, 110, 60)
        Mockito.verify(firstButton).layout(110, 0, 160, 60)
    }

    @Test
    fun testLayoutEnd_ltr_many_buttons_but_one_is_gone() {
        val firstButton = PowerMockito.mock(NavigationBarButton::class.java)
        val secondButton = PowerMockito.mock(NavigationBarButton::class.java)
        val thirdButton = PowerMockito.mock(NavigationBarButton::class.java)
        val buttons = arrayOf(firstButton, secondButton, thirdButton)

        NavigationBar::class.java
                .getDeclaredField("buttonsEnd")
                .apply { isAccessible = true }
                .set(navigationBar, buttons)

        for (button in buttons) {
            Mockito.`when`(button.visibility).thenReturn(View.VISIBLE)
            Mockito.`when`(button.measuredWidth).thenReturn(50)
        }

        Mockito.`when`(secondButton.visibility).thenReturn(View.GONE)
        Mockito.`when`(navigationBar.getEndLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(290)
        Mockito.`when`(navigationBar.layoutEnd(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        navigationBar.layoutEnd(0, 300, 0, 60, false)
        Mockito.verify(thirdButton).layout(240, 0, 290, 60)
        Mockito.verify(firstButton).layout(190, 0, 240, 60)
    }

    @Test
    fun testLayoutEnd_rtl_many_buttons_but_one_is_gone() {
        val firstButton = PowerMockito.mock(NavigationBarButton::class.java)
        val secondButton = PowerMockito.mock(NavigationBarButton::class.java)
        val thirdButton = PowerMockito.mock(NavigationBarButton::class.java)
        val buttons = arrayOf(firstButton, secondButton, thirdButton)

        NavigationBar::class.java
                .getDeclaredField("buttonsEnd")
                .apply { isAccessible = true }
                .set(navigationBar, buttons)

        for (button in buttons) {
            Mockito.`when`(button.visibility).thenReturn(View.VISIBLE)
            Mockito.`when`(button.measuredWidth).thenReturn(50)
        }

        Mockito.`when`(secondButton.visibility).thenReturn(View.GONE)
        Mockito.`when`(navigationBar.getEndLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(10)
        Mockito.`when`(navigationBar.layoutEnd(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        navigationBar.layoutEnd(0, 300, 0, 60, true)
        Mockito.verify(thirdButton).layout(10, 0, 60, 60)
        Mockito.verify(firstButton).layout(60, 0, 110, 60)
    }

    @Test
    fun testLayoutStart_ltr_all_components() {
        val contentView = PowerMockito.mock(View::class.java)

        NavigationBar::class.java
                .getDeclaredField("contentView")
                .apply { isAccessible = true }
                .set(navigationBar, contentView)

        Mockito.`when`(contentView.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(contentView.measuredWidth).thenReturn(50)
        Mockito.`when`(buttonStart.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(buttonStart.measuredWidth).thenReturn(50)
        Mockito.`when`(navigationBar.layoutStart(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()
        Mockito.`when`(navigationBar.getStartLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(10)

        navigationBar.layoutStart(0, 150, 0, 60, false)
        Mockito.verify(buttonStart).layout(10, 0, 60, 60)
        Mockito.verify(contentView).layout(60, 0, 110, 60)
    }

    @Test
    fun testLayoutStart_rtl_all_components() {
        val contentView = PowerMockito.mock(View::class.java)

        NavigationBar::class.java
                .getDeclaredField("contentView")
                .apply { isAccessible = true }
                .set(navigationBar, contentView)

        Mockito.`when`(contentView.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(contentView.measuredWidth).thenReturn(50)
        Mockito.`when`(buttonStart.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(buttonStart.measuredWidth).thenReturn(50)
        Mockito.`when`(navigationBar.layoutStart(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()
        Mockito.`when`(navigationBar.getStartLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(140)

        navigationBar.layoutStart(0, 150, 0, 60, true)
        Mockito.verify(buttonStart).layout(90, 0, 140, 60)
        Mockito.verify(contentView).layout(40, 0, 90, 60)
    }

    @Test
    fun testLayoutStart_ltr_only_with_button_start() {
        Mockito.`when`(buttonStart.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(buttonStart.measuredWidth).thenReturn(50)
        Mockito.`when`(navigationBar.layoutStart(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()
        Mockito.`when`(navigationBar.getStartLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(25)

        navigationBar.layoutStart(0, 150, 0, 60, false)
        Mockito.verify(buttonStart).layout(25, 0, 75, 60)
    }

    @Test
    fun testLayoutStart_rtl_only_with_button_start() {
        Mockito.`when`(buttonStart.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(buttonStart.measuredWidth).thenReturn(50)
        Mockito.`when`(navigationBar.layoutStart(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()
        Mockito.`when`(navigationBar.getStartLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(55)

        navigationBar.layoutStart(0, 150, 0, 60, true)
        Mockito.verify(buttonStart).layout(5, 0, 55, 60)
    }

    @Test
    fun testLayoutStart_ltr_only_with_content_view() {
        val contentView = PowerMockito.mock(View::class.java)

        NavigationBar::class.java
                .getDeclaredField("contentView")
                .apply { isAccessible = true }
                .set(navigationBar, contentView)

        Mockito.`when`(contentView.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(contentView.measuredWidth).thenReturn(50)
        Mockito.`when`(navigationBar.layoutStart(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()
        Mockito.`when`(navigationBar.getStartLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(25)

        navigationBar.layoutStart(0, 150, 0, 60, false)
        Mockito.verify(contentView).layout(25, 0, 75, 60)
    }

    @Test
    fun testLayoutStart_rtl_only_with_content_view() {
        val contentView = PowerMockito.mock(View::class.java)

        NavigationBar::class.java
                .getDeclaredField("contentView")
                .apply { isAccessible = true }
                .set(navigationBar, contentView)

        Mockito.`when`(contentView.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(contentView.measuredWidth).thenReturn(50)
        Mockito.`when`(navigationBar.layoutStart(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()
        Mockito.`when`(navigationBar.getStartLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(55)

        navigationBar.layoutStart(0, 150, 0, 60, true)
        Mockito.verify(contentView).layout(5, 0, 55, 60)
    }

    @Test
    fun testStartLeftBorder_ltr_visible_button_start() {
        val buttonsParams = NavigationBarButtonParams().apply {
            leftPadding = 15
        }

        NavigationBar::class.java
                .getDeclaredField("buttonParams")
                .apply { isAccessible = true }
                .set(navigationBar, buttonsParams)

        Mockito.`when`(navigationBar.paddingStart).thenReturn(25)
        Mockito.`when`(buttonStart.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(navigationBar.getStartLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        val result = navigationBar.getStartLeftBorder(40, 80, false)
        assertEquals(50, result)
    }

    @Test
    fun testStartLeftBorder_ltr_without_button_start() {
        val buttonsParams = NavigationBarButtonParams().apply {
            leftPadding = 15
        }

        NavigationBar::class.java
                .getDeclaredField("buttonParams")
                .apply { isAccessible = true }
                .set(navigationBar, buttonsParams)

        Mockito.`when`(navigationBar.paddingStart).thenReturn(25)
        Mockito.`when`(buttonStart.visibility).thenReturn(View.GONE)
        Mockito.`when`(navigationBar.getStartLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        val result = navigationBar.getStartLeftBorder(40, 80, false)
        assertEquals(65, result)
    }

    @Test
    fun testStartLeftBorder_rtl_visible_button_start() {
        val buttonsParams = NavigationBarButtonParams().apply {
            rightPadding = 15
        }

        NavigationBar::class.java
                .getDeclaredField("buttonParams")
                .apply { isAccessible = true }
                .set(navigationBar, buttonsParams)

        Mockito.`when`(navigationBar.paddingStart).thenReturn(25)
        Mockito.`when`(buttonStart.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(navigationBar.getStartLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        val result = navigationBar.getStartLeftBorder(40, 80, true)
        assertEquals(70, result)
    }

    @Test
    fun testStartLeftBorder_rtl_without_button_start() {
        val buttonsParams = NavigationBarButtonParams().apply {
            rightPadding = 15
        }

        NavigationBar::class.java
                .getDeclaredField("buttonParams")
                .apply { isAccessible = true }
                .set(navigationBar, buttonsParams)

        Mockito.`when`(navigationBar.paddingStart).thenReturn(25)
        Mockito.`when`(buttonStart.visibility).thenReturn(View.GONE)
        Mockito.`when`(navigationBar.getStartLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        val result = navigationBar.getStartLeftBorder(40, 80, true)
        assertEquals(55, result)
    }

    @Test
    fun testEndLeftBorder_ltr() {
        val buttonsParams = NavigationBarButtonParams().apply {
            leftPadding = 15
        }

        NavigationBar::class.java
                .getDeclaredField("buttonParams")
                .apply { isAccessible = true }
                .set(navigationBar, buttonsParams)

        Mockito.`when`(navigationBar.paddingEnd).thenReturn(25)
        Mockito.`when`(navigationBar.getEndLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        val result = navigationBar.getEndLeftBorder(40, 80, false)
        assertEquals(70, result)
    }

    @Test
    fun testEndLeftBorder_rtl() {
        val buttonsParams = NavigationBarButtonParams().apply {
            rightPadding = 15
        }

        NavigationBar::class.java
                .getDeclaredField("buttonParams")
                .apply { isAccessible = true }
                .set(navigationBar, buttonsParams)

        Mockito.`when`(navigationBar.paddingEnd).thenReturn(25)
        Mockito.`when`(navigationBar.getEndLeftBorder(anyInt(), anyInt(), anyBoolean()))
                .thenCallRealMethod()

        val result = navigationBar.getEndLeftBorder(40, 80, true)
        assertEquals(50, result)
    }
}