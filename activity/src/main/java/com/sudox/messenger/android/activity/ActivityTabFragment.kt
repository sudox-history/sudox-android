package com.sudox.messenger.android.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.moments.vos.MomentVO
import kotlinx.android.synthetic.main.fragment_activity.activityContentList

class ActivityTabFragment : TabsChildFragment(), ApplicationBarListener {

    private var counter = 7L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val firstPhoto = getDrawable(context!!, R.drawable.drawable_photo_1)!!
        val secondPhoto = getDrawable(context!!, R.drawable.drawable_photo_2)!!
        val thirdPhoto = getDrawable(context!!, R.drawable.drawable_photo_3)!!

        activityContentList.adapter = ActivityAdapter(context!!, activityContentList).apply {
            momentsAdapter.moments.apply {
                add(MomentVO(isStartViewed = true, isFullyViewed = true, publisherName = "Yaroslav", publisherPhoto = secondPhoto,
                        publishTime = 2L))
                add(MomentVO(isStartViewed = true, isFullyViewed = true, publisherName = "Vladislav", publisherPhoto = thirdPhoto,
                        publishTime = 1L))
                add(MomentVO(isStartViewed = true, isFullyViewed = true, publisherName = "Pozdnyachkov", publisherPhoto = thirdPhoto,
                        publishTime = 3L))
                add(MomentVO(isStartViewed = true, isFullyViewed = false, publisherName = "Second Yaroslav", publisherPhoto = secondPhoto,
                        publishTime = 4L))
                add(MomentVO(isStartViewed = true, isFullyViewed = false, publisherName = "Third Yaroslav", publisherPhoto = secondPhoto,
                        publishTime = 5L))
                add(MomentVO(isStartViewed = false, isFullyViewed = false, publisherName = "Fourth Yaroslav", publisherPhoto = secondPhoto,
                        publishTime = 6L))
            }

            momentsAdapter.showMomentCallback = {
                momentsAdapter.moments.add(MomentVO(
                        isStartViewed = false,
                        isFullyViewed = false,
                        publisherName = "TE Yaroslav",
                        publisherPhoto = secondPhoto,
                        publishTime = counter++))
            }

            momentsAdapter.addMomentCallback = {
                momentsAdapter.moments.removeItemAt(0)
            }

            momentsAdapter.setPublisherPhoto(firstPhoto)
        }

        activityContentList.layoutManager = LinearLayoutManager(context)
    }

    override fun getTitle(context: Context): String? {
        return context.getString(R.string.activity)
    }

    override fun prepareToShowing() {
        super.prepareToShowing()

        applicationBarManager!!.let {
            it.setListener(this)
            it.toggleIconButtonAtStart(R.drawable.ic_notifications_none)
            it.toggleIconButtonAtEnd(R.drawable.ic_search)
        }
    }

    override fun onButtonClicked(tag: Int) {}
}