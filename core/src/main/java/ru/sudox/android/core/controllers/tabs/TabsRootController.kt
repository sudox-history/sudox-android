package ru.sudox.android.core.controllers.tabs

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter
import ru.sudox.android.core.CoreController

@Suppress("LeakingThis")
abstract class TabsRootController : CoreController() {

    internal val loadedControllers = arrayOfNulls<CoreController>(getControllersCount())

    @Suppress("LeakingThis")
    private val pagerAdapter = object : RouterPagerAdapter(this) {
        override fun configureRouter(router: Router, position: Int) {
            if (!router.hasRootController()) {
                var controller = loadedControllers[position]

                if (controller == null) {
                    controller = createController(position)
                    loadedControllers[position] = controller
                }

                router.setRoot(RouterTransaction.with(controller))
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return getControllerTitle(position)
        }

        override fun getCount(): Int {
            return getControllersCount()
        }
    }

    override fun createView(container: ViewGroup, savedViewState: Bundle?): View {
        if (appBarLayoutVO !is TabsAppBarLayoutVO) {
            appBarLayoutVO = TabsAppBarLayoutVO(activity!!, appBarLayoutVO)
        }

        return ViewPager(activity!!).apply {
            adapter = pagerAdapter
        }
    }

    override fun bindView(view: View) {
        super.bindView(view)

        (appBarLayoutVO as TabsAppBarLayoutVO)
                .tabLayout
                .setupWithViewPager(view as ViewPager)
    }

    override fun isInStartState(): Boolean {
        return (view as ViewPager).currentItem == 0 && loadedControllers[0]!!.isInStartState()
    }

    override fun toStartState() {
        val viewPager = (view as ViewPager)
        val currentItem = viewPager.currentItem
        val controller = loadedControllers[currentItem]!!

        if (controller.isInStartState()) {
            viewPager.setCurrentItem(0, true)
        } else {
            controller.toStartState()
        }
    }

    abstract fun getControllersCount(): Int
    abstract fun getControllerTitle(position: Int): String
    abstract fun createController(position: Int): CoreController
}