package com.sudox.messenger.android.activity

import android.content.Context
import android.os.Handler
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.activity.adapters.ActivityTabAdapter
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.fragments.ViewListFragment
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.moments.vos.AddMomentVO
import com.sudox.messenger.android.moments.vos.MomentVO
import com.sudox.messenger.android.people.common.vos.SimplePeopleVO

class ActivityTabFragment : ViewListFragment<ViewListAdapter<*>>(), TabsChildFragment, ApplicationBarListener {

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.activity)
    }

    override fun prepareToShowing(coreFragment: CoreFragment) {
        super.prepareToShowing(coreFragment)

        applicationBarManager!!.let {
            it.setListener(this)
            it.toggleIconButtonAtStart(R.drawable.ic_notifications_none)
            it.toggleIconButtonAtEnd(R.drawable.ic_search)
        }
    }

    override fun getAdapter(viewList: ViewList): ViewListAdapter<*>? {
        val handler = Handler()

        return ActivityTabAdapter().apply {
            momentsAdapter.apply {
                addMomentVO = AddMomentVO(SimplePeopleVO(1, "kerjen", 1))

                handler.postDelayed({
                    momentsVOs.add(MomentVO(SimplePeopleVO(2, "undefined.7887", 2), 1L, false))
                    momentsVOs.add(MomentVO(SimplePeopleVO(3, "isp", 3), 2L, true))
                    momentsVOs.add(MomentVO(SimplePeopleVO(4, "Максим Митюшкин", 4), 3L, true))
                    momentsVOs.add(MomentVO(SimplePeopleVO(5, "andy", 5), 4L, false))
                    momentsVOs.add(MomentVO(SimplePeopleVO(6, "Jeremy Clarkson", 6), 5L, false))

                    handler.postDelayed({
                        momentsVOs.removeItemAt(0)
                        momentsVOs.removeItemAt(0)
                        momentsVOs.removeItemAt(0)
                        momentsVOs.removeItemAt(0)

                        handler.postDelayed({
                            momentsVOs.removeItemAt(0)

                            handler.postDelayed({
                                momentsVOs.add(MomentVO(SimplePeopleVO(2, "undefined.7887", 2), 1L, false))
                                momentsVOs.add(MomentVO(SimplePeopleVO(3, "isp", 3), 2L, true))
                                momentsVOs.add(MomentVO(SimplePeopleVO(4, "Максим Митюшкин", 4), 3L, true))
                                momentsVOs.add(MomentVO(SimplePeopleVO(5, "andy", 5), 4L, false))
                                momentsVOs.add(MomentVO(SimplePeopleVO(6, "Jeremy Clarkson", 6), 5L, false))
                            }, 2000L)
                        }, 2000L)
                    }, 4000L)
                }, 1000L)
            }
        }
    }

    override fun onButtonClicked(tag: Int) {
    }
}