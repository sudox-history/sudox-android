package com.sudox.messenger.android.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import com.sudox.design.applicationBar.ApplicationBarListener
import com.sudox.messenger.android.core.CoreActivity
import com.sudox.messenger.android.core.CoreFragment
import com.sudox.messenger.android.core.viewPager.ViewPagerFragment
import com.sudox.messenger.android.moments.vos.MomentVO
import kotlinx.android.synthetic.main.fragment_activity.activityContentList

class ActivityFragment : CoreFragment(), ViewPagerFragment, ApplicationBarListener {

    private var counter = 7L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val firstPhoto = getDrawable(context!!, R.drawable.drawable_photo_1)!!
        val secondPhoto = getDrawable(context!!, R.drawable.drawable_photo_2)!!
        val thirdPhoto = getDrawable(context!!, R.drawable.drawable_photo_3)!!

        activityContentList.adapter = ActivityAdapter(context!!).apply {
            momentsAdapter.moments.apply {
                add(MomentVO(userName = "Yaroslav", userPhoto = secondPhoto, isStartViewed = true, publishTime = 2L,
                        isFullyViewed = true))
                add(MomentVO(userName = "Vladislav", userPhoto = thirdPhoto, isStartViewed = true, publishTime = 1L,
                        isFullyViewed = true))
                add(MomentVO(userName = "Pozdnyachkov", userPhoto = thirdPhoto, isStartViewed = true, publishTime = 3L,
                        isFullyViewed = true))
                add(MomentVO(userName = "Second Yaroslav", userPhoto = secondPhoto, isStartViewed = true, publishTime = 4L,
                        isFullyViewed = false))
                add(MomentVO(userName = "Third Yaroslav", userPhoto = secondPhoto, isStartViewed = true, publishTime = 5L,
                        isFullyViewed = false))
                add(MomentVO(userName = "Fourth Yaroslav", userPhoto = secondPhoto, isStartViewed = false, publishTime = 6L,
                        isFullyViewed = false))
            }

            momentsAdapter.showMomentCallback = {
                momentsAdapter.moments.add(MomentVO(
                        userName = "TE Yaroslav",
                        userPhoto = secondPhoto,
                        isStartViewed = false,
                        publishTime = counter++,
                        isFullyViewed = false))
            }

            momentsAdapter.addMomentCallback = {
                momentsAdapter.moments.removeItemAt(0)
            }

            momentsAdapter.setUserPhoto(firstPhoto)
        }

        activityContentList.layoutManager = LinearLayoutManager(context)
    }

    override fun onButtonClicked(tag: Int) {}

    override fun onPageSelected(activity: CoreActivity) {
        activity.getApplicationBarManager().let {
            it.reset(false)
            it.setListener(this)
            it.toggleIconButtonAtStart(R.drawable.ic_notifications_none)
            it.toggleIconButtonAtEnd(R.drawable.ic_search)
        }
    }

    override fun getPageTitle(context: Context): CharSequence? {
        return context.getString(R.string.activity)
    }
}