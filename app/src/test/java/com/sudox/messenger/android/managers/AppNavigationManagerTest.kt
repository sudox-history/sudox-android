package com.sudox.messenger.android.managers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sudox.messenger.android.R
import com.sudox.messenger.android.TestRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

private const val STATE_KEY = "KEY"
private const val STATE_VALUE = "VALUE"

@RunWith(TestRunner::class)
class AppNavigationManagerTest : Assert() {

    private var activityController: ActivityController<AppCompatActivity>? = null
    private var navigationManager: AppNavigationManager? = null

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
                .buildActivity(AppCompatActivity::class.java)
                .apply {
                    if (activityController != null) {
                        create(state)
                    } else {
                        create()
                    }
                }.start()

        val activity = activityController!!.get()

        activity.setContentView(R.layout.activity_app)

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        navigationManager = AppNavigationManager(
                activity.supportFragmentManager,
                R.id.frameContainer
        )

        activityController!!
                .resume()
                .visible()
    }

    @Test
    fun testManyFragmentShowing() {
        val firstFragment = Fragment()
        val secondFragment = Fragment()

        navigationManager!!.showFragment(firstFragment, false)
        navigationManager!!.showFragment(secondFragment, false)

        assertEquals(secondFragment, navigationManager!!.getCurrentFragment())
    }

    @Test
    fun testPreviousFragmentShowing() {
        val firstFragment = Fragment()
        val secondFragment = Fragment()

        navigationManager!!.showFragment(firstFragment, true)
        navigationManager!!.showFragment(secondFragment, true)

        assertTrue(navigationManager!!.showPreviousFragment())
        Robolectric.flushBackgroundThreadScheduler()

        assertEquals(firstFragment, navigationManager!!.getCurrentFragment())
    }

    @Test
    fun testThatSingleFragmentNotClosed() {
        val firstFragment = Fragment()
        val secondFragment = Fragment()

        navigationManager!!.showFragment(firstFragment, true)
        navigationManager!!.showFragment(secondFragment, true)
        navigationManager!!.showPreviousFragment()
        Robolectric.flushBackgroundThreadScheduler()

        assertFalse(navigationManager!!.showPreviousFragment())
        assertEquals(firstFragment, navigationManager!!.getCurrentFragment())
    }

    @Test
    fun testBackstackClearing() {
        val firstFragment = Fragment()
        val secondFragment = Fragment()
        val thirdFragment = Fragment()

        navigationManager!!.showFragment(firstFragment, true)
        navigationManager!!.showFragment(secondFragment, true)
        navigationManager!!.clearBackstack()
        navigationManager!!.showFragment(thirdFragment, true)
        Robolectric.flushBackgroundThreadScheduler()

        assertFalse(navigationManager!!.showPreviousFragment())
        assertEquals(thirdFragment, navigationManager!!.getCurrentFragment())
    }

    @Test
    fun testStateSaving() {
        val fragment = TestFragment()
        val bundle = Bundle()

        navigationManager!!.showFragment(fragment, true)
        navigationManager!!.saveState(bundle)
        Robolectric.flushBackgroundThreadScheduler()

        createActivity()
        navigationManager!!.restoreState(bundle)
        Robolectric.flushBackgroundThreadScheduler()

        val current = navigationManager!!.getCurrentFragment() as? TestFragment
        assertNotNull(current)
        assertEquals(STATE_VALUE, current!!.value)
    }

    class TestFragment : Fragment() {

        internal var value: String? = null

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            value = savedInstanceState?.getString(STATE_KEY)
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putString(STATE_KEY, STATE_VALUE)
        }
    }
}