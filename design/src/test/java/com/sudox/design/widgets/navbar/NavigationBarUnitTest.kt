package com.sudox.design.widgets.navbar

import com.sudox.design.widgets.navbar.button.NavigationBarButton
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(NavigationBar::class, NavigationBarButton::class)
class NavigationBarUnitTest : Assert() {

    private lateinit var navigationBar: NavigationBar
    private lateinit var buttonStart: NavigationBarButton

    @Before
    fun setUp() {
        navigationBar = Mockito.mock(NavigationBar::class.java)
        buttonStart = Mockito.mock(NavigationBarButton::class.java)

        NavigationBar::class.java
                .getDeclaredField("buttonStart")
                .apply { isAccessible = true }
                .set(navigationBar, buttonStart)
    }
}