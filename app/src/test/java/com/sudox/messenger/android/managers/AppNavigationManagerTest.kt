package com.sudox.messenger.android.managers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sudox.messenger.android.R
import com.sudox.messenger.android.TestRunner
import kotlinx.android.synthetic.main.activity_app.navigationBar
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
                activity,
                activity.supportFragmentManager,
                activity.navigationBar,
                R.id.frameContainer
        )

        activityController!!.resume().visible()
    }

    @Test
    fun testManyFragmentReplacing() {
        val firstFragment = Fragment()
        val secondFragment = Fragment()

        navigationManager!!.replaceFragment(firstFragment, false)
        navigationManager!!.replaceFragment(secondFragment, false)

        assertEquals(secondFragment, navigationManager!!.getCurrentFragment())
    }

    @Test
    fun testStateSaving() {
        val fragment = TestFragment()
        val bundle = Bundle()

        navigationManager!!.replaceFragment(fragment, true)
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