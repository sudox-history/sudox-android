package com.sudox.design.tabLayout

import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sudox.design.DesignTestContainer
import com.sudox.design.DesignTestRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(DesignTestRunner::class)
class TabLayoutTest : Assert() {

    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var container: DesignTestContainer<LinearLayout>? = null

    @Before
    fun setUp() {
        container = DesignTestContainer {
            tabLayout = TabLayout(it).apply { id = Int.MAX_VALUE - 2 }
            viewPager = ViewPager(it).apply {
                adapter = Adapter(it.supportFragmentManager)
                id = Int.MAX_VALUE - 1
            }

            LinearLayout(it).apply {
                id = Int.MAX_VALUE

                addView(viewPager)
                addView(tabLayout)
            }
        }

        container!!.fill()
    }

    @Test
    fun testPagerLinking() {
        viewPager!!.currentItem = 1
        tabLayout!!.setViewPager(viewPager!!)

        val firstButton = tabLayout!!.getChildAt(0) as TabLayoutButton
        val secondButton = tabLayout!!.getChildAt(1) as TabLayoutButton

        assertFalse(firstButton.isActive())
        assertTrue(secondButton.isActive())

        assertEquals("Title 1", firstButton.getText())
        assertEquals("Title 2", secondButton.getText())
    }

    @Test
    fun testPageSelection() {
        tabLayout!!.setViewPager(viewPager!!)

        val firstButton = tabLayout!!.getChildAt(0) as TabLayoutButton
        val secondButton = tabLayout!!.getChildAt(1) as TabLayoutButton

        secondButton.callOnClick()
        assertFalse(firstButton.isActive())
        assertTrue(secondButton.isActive())

        firstButton.callOnClick()
        assertTrue(firstButton.isActive())
        assertFalse(secondButton.isActive())
    }

    @Test
    fun testPageScrolling() {
        tabLayout!!.setViewPager(viewPager!!)

        val firstButton = tabLayout!!.getChildAt(0) as TabLayoutButton
        val secondButton = tabLayout!!.getChildAt(1) as TabLayoutButton

        tabLayout!!.onPageScrollStateChanged(ViewPager.SCROLL_STATE_DRAGGING)
        tabLayout!!.onPageScrollStateChanged(ViewPager.SCROLL_STATE_SETTLING)

        tabLayout!!.onPageScrolled(0, 0.1F, 0)
        assertTrue(firstButton.isActive())
        assertFalse(secondButton.isActive())

        tabLayout!!.onPageScrolled(0, 0.5F, 0)
        assertFalse(firstButton.isActive())
        assertTrue(secondButton.isActive())

        tabLayout!!.onPageScrolled(0, 0.9F, 0)
        assertFalse(firstButton.isActive())
        assertTrue(secondButton.isActive())

        tabLayout!!.onPageSelected(1)
        tabLayout!!.onPageScrolled(1, 0.0F, 0)
        assertFalse(firstButton.isActive())
        assertTrue(secondButton.isActive())

        tabLayout!!.onPageScrolled(1, 0.9F, 0)
        assertFalse(firstButton.isActive())
        assertTrue(secondButton.isActive())

        tabLayout!!.onPageScrolled(1, 0.5F, 0)
        assertFalse(firstButton.isActive())
        assertTrue(secondButton.isActive())

        tabLayout!!.onPageScrolled(0, 0.1F, 0)
        assertTrue(firstButton.isActive())
        assertFalse(secondButton.isActive())

        tabLayout!!.onPageSelected(0)
        tabLayout!!.onPageScrolled(0, 0.0F, 0)
        assertTrue(firstButton.isActive())
        assertFalse(secondButton.isActive())
    }

    class Adapter(
            fragmentManager: FragmentManager
    ) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragments = arrayOf(
                Pair("Title 1", Fragment()),
                Pair("Title 2", Fragment())
        )

        override fun getPageTitle(position: Int): CharSequence? {
            return fragments[position].first
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position].second
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }
}