package com.sudox.messenger.android.managers

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.sudox.design.navigationBar.NavigationBar
import com.sudox.messenger.android.TestActivity
import com.sudox.messenger.android.TestRunner
import com.sudox.messenger.android.auth.code.AuthCodeFragment
import com.sudox.messenger.android.auth.phone.AuthPhoneFragment
import com.sudox.messenger.android.core.managers.NavigationManager
import com.sudox.messenger.android.people.ChildFragment
import com.sudox.messenger.android.people.PeopleFragment
import com.sudox.messenger.android.people.ProfileFragment
import com.sudox.messenger.android.people.WorldFragment
import kotlinx.android.synthetic.main.activity_app.frameContainer
import kotlinx.android.synthetic.main.activity_app.navigationBar
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

@RunWith(TestRunner::class)
class AppNavigationManagerTest : Assert() {

    private var navigationBar: NavigationBar? = null
    private var activityController: ActivityController<TestActivity>? = null
    private var navigationManager: NavigationManager? = null
    private var fragmentManager: FragmentManager? = null
    private var frameLayout: FrameLayout? = null

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
                .buildActivity(TestActivity::class.java)
                .apply {
                    if (activityController != null) {
                        create(state)
                    } else {
                        create()
                    }
                }.start()

        val activity = activityController!!.get()

        state?.let {
            activityController!!.restoreInstanceState(state)
        }

        frameLayout = activity.frameContainer
        fragmentManager = activity.supportFragmentManager
        navigationBar = activity.navigationBar
        navigationManager = activity.getNavigationManager()

        activityController!!.resume().visible()
    }

    @Test
    fun testAuthPartShowing() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showAuthPart()

        assertEquals(1, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0] is AuthPhoneFragment)

        assertEquals(View.GONE, navigationBar!!.visibility)
        assertFalse(navigationManager!!.popBackstack())
    }

    @Test
    fun testMainPartShowing() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showMainPart()

        assertEquals(4, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0] is PeopleFragment)
        assertTrue(fragmentManager!!.fragments[1] is MessagesFragment)
        assertTrue(fragmentManager!!.fragments[2] is WorldFragment)
        assertTrue(fragmentManager!!.fragments[3] is ProfileFragment)

        assertTrue(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)

        assertEquals(View.VISIBLE, navigationBar!!.visibility)
        assertFalse(navigationManager!!.popBackstack())
    }

    @Test
    fun testAuthToMainPartChanging() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showAuthPart()
        navigationManager!!.showMainPart()

        assertEquals(4, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0] is PeopleFragment)
        assertTrue(fragmentManager!!.fragments[1] is MessagesFragment)
        assertTrue(fragmentManager!!.fragments[2] is WorldFragment)
        assertTrue(fragmentManager!!.fragments[3] is ProfileFragment)

        assertTrue(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)

        assertEquals(View.VISIBLE, navigationBar!!.visibility)
        assertFalse(navigationManager!!.popBackstack())
    }

    @Test
    fun testMainToAuthPartChanging() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showMainPart()
        navigationManager!!.showAuthPart()

        assertEquals(1, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0] is AuthPhoneFragment)

        assertEquals(View.GONE, navigationBar!!.visibility)
        assertFalse(navigationManager!!.popBackstack())
    }

    @Test
    fun testChildFragmentShowingInAuthPart() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showAuthPart()
        navigationManager!!.showChildFragment(AuthCodeFragment())

        assertEquals(2, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0] is AuthPhoneFragment)
        assertTrue(fragmentManager!!.fragments[1] is AuthCodeFragment)
        assertFalse(fragmentManager!!.fragments[0].isVisible)
        assertTrue(fragmentManager!!.fragments[1].isVisible)

        assertTrue(navigationManager!!.popBackstack())
        assertEquals(1, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0].isVisible)

        assertFalse(navigationManager!!.popBackstack())
    }

    @Test
    fun testChildFragmentShowingInMainPart() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showMainPart()
        navigationManager!!.showChildFragment(ChildFragment())

        assertEquals(5, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0] is PeopleFragment)
        assertTrue(fragmentManager!!.fragments[1] is MessagesFragment)
        assertTrue(fragmentManager!!.fragments[2] is WorldFragment)
        assertTrue(fragmentManager!!.fragments[3] is ProfileFragment)
        assertTrue(fragmentManager!!.fragments[4] is ChildFragment)

        assertFalse(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)
        assertTrue(fragmentManager!!.fragments[4].isVisible)

        assertTrue(navigationManager!!.popBackstack())
        assertEquals(4, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)

        assertFalse(navigationManager!!.popBackstack())
    }

    @Test
    fun testNavigationBarBackstack() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showMainPart()
        navigationManager!!.showChildFragment(ChildFragment())
        navigationBar!!.setSelectedItem(PROFILE_NAVBAR_ITEM_ID)
        navigationBar!!.setSelectedItem(PEOPLE_NAVBAR_ITEM_ID)

        assertEquals(5, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0] is PeopleFragment)
        assertTrue(fragmentManager!!.fragments[1] is MessagesFragment)
        assertTrue(fragmentManager!!.fragments[2] is WorldFragment)
        assertTrue(fragmentManager!!.fragments[3] is ProfileFragment)
        assertTrue(fragmentManager!!.fragments[4] is ChildFragment)

        assertFalse(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)
        assertTrue(fragmentManager!!.fragments[4].isVisible)
    }

    @Test
    fun testBackingFromChildToRootFragment() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showMainPart()
        navigationManager!!.showChildFragment(ChildFragment())
        navigationBar!!.setSelectedItem(PEOPLE_NAVBAR_ITEM_ID)

        assertEquals(4, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)

        assertFalse(navigationManager!!.popBackstack())
    }

    @Test
    fun testBackingFrom2stChildToRootFragment() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showMainPart()
        navigationManager!!.showChildFragment(ChildFragment())
        navigationManager!!.showChildFragment(ChildFragment())
        navigationBar!!.setSelectedItem(PEOPLE_NAVBAR_ITEM_ID)

        assertEquals(4, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)

        assertFalse(navigationManager!!.popBackstack())
    }

    @Test
    fun checkBackstackPriority() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showMainPart()
        navigationManager!!.showChildFragment(ChildFragment())
        navigationBar!!.setSelectedItem(PROFILE_NAVBAR_ITEM_ID)
        navigationBar!!.setSelectedItem(PEOPLE_NAVBAR_ITEM_ID)

        assertTrue(navigationManager!!.popBackstack())
        assertEquals(4, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)

        assertTrue(navigationManager!!.popBackstack())
        assertEquals(4, fragmentManager!!.fragments.size)
        assertFalse(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertTrue(fragmentManager!!.fragments[3].isVisible)

        assertFalse(navigationManager!!.popBackstack())
    }

    @Test
    fun checkBackstackPolicy() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showMainPart()
        navigationBar!!.setSelectedItem(DIALOGS_NAVBAR_ITEM_ID)
        navigationBar!!.setSelectedItem(WORLD_NAVBAR_ITEM_ID)
        navigationBar!!.setSelectedItem(PROFILE_NAVBAR_ITEM_ID)
        navigationBar!!.setSelectedItem(PEOPLE_NAVBAR_ITEM_ID)
        navigationManager!!.showChildFragment(ChildFragment())
        navigationBar!!.setSelectedItem(DIALOGS_NAVBAR_ITEM_ID)
        navigationBar!!.setSelectedItem(WORLD_NAVBAR_ITEM_ID)
        navigationBar!!.setSelectedItem(PROFILE_NAVBAR_ITEM_ID)

        assertTrue(navigationManager!!.popBackstack())
        assertEquals(5, fragmentManager!!.fragments.size)
        assertFalse(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertTrue(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)
        assertFalse(fragmentManager!!.fragments[4].isVisible)

        assertTrue(navigationManager!!.popBackstack())
        assertEquals(5, fragmentManager!!.fragments.size)
        assertFalse(fragmentManager!!.fragments[0].isVisible)
        assertTrue(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)
        assertFalse(fragmentManager!!.fragments[4].isVisible)

        assertTrue(navigationManager!!.popBackstack())
        assertEquals(5, fragmentManager!!.fragments.size)
        assertFalse(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)
        assertTrue(fragmentManager!!.fragments[4].isVisible)

        assertTrue(navigationManager!!.popBackstack())
        assertEquals(4, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)

        assertFalse(navigationManager!!.popBackstack())
    }

    @Test
    fun testStateSaving() {
        navigationManager!!.configureNavigationBar()
        navigationManager!!.showMainPart()
        navigationManager!!.showChildFragment(ChildFragment())
        createActivity()

        assertEquals(5, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0] is PeopleFragment)
        assertTrue(fragmentManager!!.fragments[1] is MessagesFragment)
        assertTrue(fragmentManager!!.fragments[2] is WorldFragment)
        assertTrue(fragmentManager!!.fragments[3] is ProfileFragment)
        assertTrue(fragmentManager!!.fragments[4] is ChildFragment)

        assertFalse(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)
        assertTrue(fragmentManager!!.fragments[4].isVisible)

        assertTrue(navigationManager!!.popBackstack())
        assertEquals(4, fragmentManager!!.fragments.size)
        assertTrue(fragmentManager!!.fragments[0].isVisible)
        assertFalse(fragmentManager!!.fragments[1].isVisible)
        assertFalse(fragmentManager!!.fragments[2].isVisible)
        assertFalse(fragmentManager!!.fragments[3].isVisible)

        assertFalse(navigationManager!!.popBackstack())
    }
}