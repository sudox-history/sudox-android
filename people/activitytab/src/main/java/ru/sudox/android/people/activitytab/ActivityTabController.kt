package ru.sudox.android.people.activitytab

import android.os.Handler
import ru.sudox.android.core.controllers.ViewListController
import ru.sudox.android.media.vos.impls.ImageAttachmentVO
import ru.sudox.android.moments.vos.impl.add.AddMomentVO
import ru.sudox.android.moments.vos.impl.MomentVO
import ru.sudox.android.news.vos.NewsVO
import ru.sudox.android.people.activitytab.adapters.ActivityTabAdapter
import ru.sudox.design.viewlist.ViewList

class ActivityTabController : ViewListController<ActivityTabAdapter>() {

    override fun getAdapter(viewList: ViewList): ActivityTabAdapter? {
        val handler = Handler()

        return ActivityTabAdapter(glide).apply {
            momentsAdapter.apply {
                addMomentVO = AddMomentVO(1, "kerjen", 1)

                handler.postDelayed({
                    momentsVOs.add(MomentVO(2, "undefined.7887", 2, 1L, false))
                    newsVOs.add(NewsVO(4L, "Максим Митюшкин", 4L, true, false, 1_000_000_000, 400_000, 1456, 0, arrayListOf(
                            ImageAttachmentVO(7L).apply {
                                height = 387
                                width = 620
                            }
                    ), System.currentTimeMillis(), null))
                }, 5000L)

                handler.postDelayed({
                    momentsVOs.add(MomentVO(3, "isp", 3, 2L, true))
                    momentsVOs.add(MomentVO(4, "Максим Митюшкин", 4, 3L, true))
                    momentsVOs.add(MomentVO(5, "andy", 5, 4L, false))
                    momentsVOs.add(MomentVO(6, "Jeremy Clarkson", 6, 5L, false))

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
                    momentsVOs.add(MomentVO(3, "isp", 3, 2L, true))
                    momentsVOs.add(MomentVO(4, "Максим Митюшкин", 4, 3L, true))
                    momentsVOs.add(MomentVO(5, "andy", 5, 4L, false))
                    momentsVOs.add(MomentVO(6, "Jeremy Clarkson", 6, 5L, false))
                }, 18000L)

                handler.postDelayed({
                    newsVOs.removeItemAt(2)
                }, 25000L)
            }
        }
    }

    override fun isChild(): Boolean {
        return true
    }
}