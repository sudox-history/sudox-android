package ru.sudox.android.core.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(CommonUiRunner::class)
class NavigationHelpersTest {

    @Test
    fun testBackstackOnNestedFragments() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java)
        val activity = controller.get()
        val firstFragment = Fragment(R.layout.layout_container)
        val secondFragment = Fragment(R.layout.layout_container)
        val firstNestedFragment = Fragment(R.layout.layout_container)
        val secondNestedFragment = Fragment(R.layout.layout_container)
        val thirdNestedFragment = Fragment(R.layout.layout_container)
        val fourthNestedFragment = Fragment(R.layout.layout_container)

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
            .replace(R.id.container, firstNestedFragment, "3")
            .addToBackStack(null)
            .commit()

        secondFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, secondNestedFragment, "4")
            .addToBackStack(null)
            .commit()

        secondNestedFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, thirdNestedFragment, "5")
            .addToBackStack(null)
            .commit()

        secondNestedFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, fourthNestedFragment, "6")
            .addToBackStack(null)
            .commit()

        assertFalse(firstFragment.isVisible)
        assertTrue(secondFragment.isVisible)
        assertFalse(firstNestedFragment.isVisible)
        assertTrue(secondNestedFragment.isVisible)
        assertFalse(thirdNestedFragment.isVisible)
        assertTrue(fourthNestedFragment.isVisible)

        assertTrue(popBackStack(activity.supportFragmentManager))
        assertFalse(firstFragment.isVisible)
        assertTrue(secondFragment.isVisible)
        assertFalse(firstNestedFragment.isVisible)
        assertTrue(secondNestedFragment.isVisible)
        assertTrue(thirdNestedFragment.isVisible)
        assertFalse(fourthNestedFragment.isVisible)

        assertTrue(popBackStack(activity.supportFragmentManager))
        assertFalse(firstFragment.isVisible)
        assertTrue(secondFragment.isVisible)
        assertTrue(firstNestedFragment.isVisible)
        assertFalse(secondNestedFragment.isVisible)
        assertFalse(thirdNestedFragment.isVisible)
        assertFalse(fourthNestedFragment.isVisible)

        assertTrue(popBackStack(activity.supportFragmentManager))
        assertTrue(firstFragment.isVisible)
        assertFalse(secondFragment.isVisible)
        assertFalse(firstNestedFragment.isVisible)
        assertFalse(secondNestedFragment.isVisible)
        assertFalse(thirdNestedFragment.isVisible)
        assertFalse(fourthNestedFragment.isVisible)

        assertFalse(popBackStack(activity.supportFragmentManager))
    }

    @Test
    fun testBackstackWhenContainsFlowFragment() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java)
        val activity = controller.get()
        val firstFragment = Fragment(R.layout.layout_container)
        val firstNestedFragment = Fragment(R.layout.layout_container)
        val secondNestedFragment = Fragment(R.layout.layout_container)
        val thirdNestedFragment = Fragment(R.layout.layout_container)

        activity.setContentView(R.layout.layout_container)
        controller.setup()

        activity
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, firstFragment, "1")
            .commit()

        firstFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, firstNestedFragment, "2")
            .commit()

        firstNestedFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, secondNestedFragment, "3")
            .addToBackStack(null)
            .commit()

        firstNestedFragment
            .childFragmentManager
            .beginTransaction()
            .replace(R.id.container, thirdNestedFragment, "4")
            .addToBackStack(null)
            .commit()

        assertTrue(popBackStack(activity.supportFragmentManager))
        assertTrue(firstFragment.isVisible)
        assertFalse(thirdNestedFragment.isVisible)
        assertTrue(firstNestedFragment.isVisible)
    }
}