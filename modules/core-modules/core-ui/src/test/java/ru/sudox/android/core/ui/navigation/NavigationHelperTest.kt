package ru.sudox.android.core.ui.navigation

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import ru.sudox.android.core.ui.CommonUiRunner
import ru.sudox.android.core.ui.R
import java.util.*

@RunWith(CommonUiRunner::class)
class NavigationHelperTest : Assert() {

    @Test
    fun testIntegrationWithBottomNavigationView() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java)
        val activity = controller.get()

        val fragmentContainer = FrameLayout(activity)
        val container = FrameLayout(activity).apply { id = View.generateViewId() }
        val bottomNavigationView = BottomNavigationView(activity).apply {
            id = View.generateViewId()

            menu.add(0, View.generateViewId(), 1, "1")
            menu.add(0, View.generateViewId(), 2, "2")
            menu.add(0, View.generateViewId(), 3, "3")
        }

        activity.setContentView(container)
        container.addView(fragmentContainer)
        container.addView(bottomNavigationView)
        controller.setup()

        val backstack = Stack<Int>()

        bottomNavigationView.setupWithFragmentManager(backstack, container.id, activity.supportFragmentManager) {
            when (it) {
                1 -> Fragment(R.layout.layout_container)
                2 -> Fragment(R.layout.layout_container)
                else -> Fragment(R.layout.layout_container)
            }
        }

        val firstFragmentTag = bottomNavigationView.menu.getItem(0).itemId
        val firstFragment = activity.supportFragmentManager.findFragmentByTag(firstFragmentTag.toString())

        assertEquals(0, backstack.size)
        assertTrue(firstFragment!!.isVisible)

        val secondFragmentTag = bottomNavigationView.menu.getItem(1).itemId
        bottomNavigationView.selectedItemId = secondFragmentTag
        val secondFragment = activity.supportFragmentManager.findFragmentByTag(secondFragmentTag.toString())

        assertEquals(1, backstack.size)
        assertEquals(firstFragmentTag, backstack[0])
        assertFalse(firstFragment.isVisible)
        assertTrue(secondFragment!!.isVisible)

        val thirdFragmentTag = bottomNavigationView.menu.getItem(2).itemId
        bottomNavigationView.selectedItemId = thirdFragmentTag
        val thirdFragment = activity.supportFragmentManager.findFragmentByTag(thirdFragmentTag.toString())

        assertEquals(2, backstack.size)
        assertEquals(firstFragmentTag, backstack[0])
        assertEquals(secondFragmentTag, backstack[1])
        assertFalse(firstFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertTrue(thirdFragment!!.isVisible)

        bottomNavigationView.selectedItemId = secondFragmentTag

        assertEquals(2, backstack.size)
        assertEquals(firstFragmentTag, backstack[0])
        assertEquals(thirdFragmentTag, backstack[1])
        assertFalse(firstFragment.isVisible)
        assertTrue(secondFragment.isVisible)
        assertFalse(thirdFragment.isVisible)

        bottomNavigationView.selectedItemId = firstFragmentTag

        assertEquals(3, backstack.size)
        assertEquals(firstFragmentTag, backstack[0])
        assertEquals(thirdFragmentTag, backstack[1])
        assertEquals(secondFragmentTag, backstack[2])
        assertTrue(firstFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertFalse(thirdFragment.isVisible)

        bottomNavigationView.selectedItemId = secondFragmentTag

        assertEquals(3, backstack.size)
        assertEquals(firstFragmentTag, backstack[0])
        assertEquals(thirdFragmentTag, backstack[1])
        assertEquals(firstFragmentTag, backstack[2])
        assertFalse(firstFragment.isVisible)
        assertTrue(secondFragment.isVisible)
        assertFalse(thirdFragment.isVisible)
    }

    @Test
    fun checkThatContainerOnBackPressedCalledAndRemovedFromManager() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java)
        val activity = controller.get()

        val firstFragment = Fragment(R.layout.layout_container)
        val secondFragment = TestContainerFragment()
        val thirdFragment = TestContainerFragment()

        activity.setContentView(R.layout.layout_container)
        controller.setup()

        activity
            .supportFragmentManager
            .beginTransaction()
            .add(R.id.container, firstFragment, "1")
            .commit()

        activity
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, secondFragment, "2")
            .addToBackStack(null)
            .commit()

        secondFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, thirdFragment, "3")
            .commit()

        thirdFragment.onBackPressedResult = false

        assertTrue(popBackstack(activity.supportFragmentManager))
        assertEquals(1, thirdFragment.onBackPressedCallsCount)
        assertEquals(0, secondFragment.onBackPressedCallsCount)
        assertFalse(thirdFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertTrue(firstFragment.isVisible)

        assertFalse(popBackstack(activity.supportFragmentManager))
    }

    @Test
    fun checkThatNestedBackstackPopped() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java)
        val activity = controller.get()

        val firstFragment = Fragment(R.layout.layout_container)
        val secondFragment = Fragment(R.layout.layout_container)
        val thirdFragment = Fragment(R.layout.layout_container)
        val fourthFragment = Fragment(R.layout.layout_container)
        val fifthFragment = Fragment(R.layout.layout_container)
        val sixthFragment = Fragment(R.layout.layout_container)
        val seventhFragment = Fragment(R.layout.layout_container)
        val eighthFragment = Fragment(R.layout.layout_container)

        activity.setContentView(R.layout.layout_container)
        controller.setup()

        activity
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, firstFragment)
            .commit()

        activity
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, secondFragment)
            .addToBackStack(null)
            .commit()

        activity
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, thirdFragment, "2")
            .addToBackStack(null)
            .commit()

        thirdFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, fourthFragment)
            .commit()

        thirdFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, fifthFragment)
            .addToBackStack(null)
            .commit()

        fifthFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, sixthFragment)
            .commit()

        sixthFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, seventhFragment)
            .commit()

        sixthFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, eighthFragment)
            .addToBackStack(null)
            .commit()

        assertTrue(popBackstack(activity.supportFragmentManager))
        assertFalse(firstFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertTrue(thirdFragment.isVisible)
        assertFalse(fourthFragment.isVisible)
        assertTrue(fifthFragment.isVisible)
        assertTrue(sixthFragment.isVisible)
        assertTrue(seventhFragment.isVisible)
        assertFalse(eighthFragment.isVisible)

        assertTrue(popBackstack(activity.supportFragmentManager))
        assertFalse(firstFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertTrue(thirdFragment.isVisible)
        assertTrue(fourthFragment.isVisible)
        assertFalse(fifthFragment.isVisible)
        assertFalse(sixthFragment.isVisible)
        assertFalse(seventhFragment.isVisible)
        assertFalse(eighthFragment.isVisible)

        assertTrue(popBackstack(activity.supportFragmentManager))
        assertFalse(firstFragment.isVisible)
        assertTrue(secondFragment.isVisible)
        assertFalse(thirdFragment.isVisible)
        assertFalse(fourthFragment.isVisible)
        assertFalse(fifthFragment.isVisible)
        assertFalse(sixthFragment.isVisible)
        assertFalse(seventhFragment.isVisible)
        assertFalse(eighthFragment.isVisible)

        assertTrue(popBackstack(activity.supportFragmentManager))
        assertTrue(firstFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertFalse(thirdFragment.isVisible)
        assertFalse(fourthFragment.isVisible)
        assertFalse(fifthFragment.isVisible)
        assertFalse(sixthFragment.isVisible)
        assertFalse(seventhFragment.isVisible)
        assertFalse(eighthFragment.isVisible)

        assertFalse(popBackstack(activity.supportFragmentManager))
    }

    class TestContainerFragment : Fragment(R.layout.layout_container), ContainerFragment {
        var onBackPressedCallsCount: Int = 0
        var onBackPressedResult = false

        override fun onBackPressed(): Boolean {
            onBackPressedCallsCount++
            return onBackPressedResult
        }
    }
}