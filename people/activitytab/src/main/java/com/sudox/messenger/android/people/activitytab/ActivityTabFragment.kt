package com.sudox.messenger.android.people.activitytab

import android.content.Context
import android.os.Handler
import com.sudox.design.viewlist.ViewList
import com.sudox.design.viewlist.ViewListAdapter
import com.sudox.messenger.android.people.activitytab.adapters.ActivityTabAdapter
import com.sudox.messenger.android.people.activitytab.vos.ActivityTabAppBarVO
import com.sudox.messenger.android.core.fragments.ViewListFragment
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.media.vos.impls.ImageAttachmentVO
import com.sudox.messenger.android.moments.vos.AddMomentVO
import com.sudox.messenger.android.moments.vos.MomentVO
import com.sudox.messenger.android.news.vos.NewsVO
import com.sudox.messenger.android.people.R
import com.sudox.messenger.android.people.common.vos.SimplePeopleVO

class ActivityTabFragment : ViewListFragment<ActivityTabAdapter>(), TabsChildFragment {

    init {
        appBarVO = ActivityTabAppBarVO()
    }

    override fun getAdapter(viewList: ViewList): ActivityTabAdapter? {
        val handler = Handler()

        return ActivityTabAdapter().apply {
            momentsAdapter.apply {
                addMomentVO = AddMomentVO(SimplePeopleVO(1, "kerjen", 1))

                handler.postDelayed({
                    momentsVOs.add(MomentVO(SimplePeopleVO(2, "undefined.7887", 2), 1L, false))
                    newsVOs.add(NewsVO(4L, "Максим Митюшкин", 4L, true, false, 1_000_000_000, 400_000, 1456, 0, arrayListOf(
                            ImageAttachmentVO(7L).apply {
                                height = 387
                                width = 620
                            }
                    ), System.currentTimeMillis(), null))
                }, 5000L)

                handler.postDelayed({
                    momentsVOs.add(MomentVO(SimplePeopleVO(3, "isp", 3), 2L, true))
                    momentsVOs.add(MomentVO(SimplePeopleVO(4, "Максим Митюшкин", 4), 3L, true))
                    momentsVOs.add(MomentVO(SimplePeopleVO(5, "andy", 5), 4L, false))
                    momentsVOs.add(MomentVO(SimplePeopleVO(6, "Jeremy Clarkson", 6), 5L, false))

                    newsVOs.add(NewsVO(4L, "Максим Митюшкин", 4L, false, false, 600_000, 400_000, 1456, 0, arrayListOf(
                            ImageAttachmentVO(8L).apply {
                                height = 1800
                                width = 2880
                            }, ImageAttachmentVO(9L).apply {
                        height = 720
                        width = 1280
                    }
                    ), System.currentTimeMillis(), "Как насчет форсирования M760Li до 900 сил? Вроде бы мотор довольно тяговитый," +
                            " но иногда хочется большего ...\n#bmw #bmw_fans #m760li #7series"
                    ))
                }, 10000L)

                handler.postDelayed({
                    momentsVOs.removeItemAt(4)

                    newsVOs.add(NewsVO(2L, "undefined.7887", 2L, false, true, 400_000, 400_000, 1456, 0, null, System.currentTimeMillis(),
                            "Мда, как только люди могут интересоваться машинами?\n@kerjen @kotlinovsky"
                    ))
                }, 12000L)

                handler.postDelayed({
                    momentsVOs.removeItemAt(0)
                }, 15000L)

                handler.postDelayed({
                    momentsVOs.beginBatchedUpdates()
                    momentsVOs.removeItemAt(0)
                    momentsVOs.removeItemAt(0)
                    momentsVOs.removeItemAt(0)
                    momentsVOs.endBatchedUpdates()
                }, 15000L)

                handler.postDelayed({
                    momentsVOs.add(MomentVO(SimplePeopleVO(3, "isp", 3), 2L, true))
                    momentsVOs.add(MomentVO(SimplePeopleVO(4, "Максим Митюшкин", 4), 3L, true))
                    momentsVOs.add(MomentVO(SimplePeopleVO(5, "andy", 5), 4L, false))
                    momentsVOs.add(MomentVO(SimplePeopleVO(6, "Jeremy Clarkson", 6), 5L, false))
                }, 18000L)

                handler.postDelayed({
                    newsVOs.removeItemAt(2)
                }, 25000L)
            }
        }
    }

    override fun getTitle(context: Context): String {
        return context.getString(R.string.activity)
    }
}