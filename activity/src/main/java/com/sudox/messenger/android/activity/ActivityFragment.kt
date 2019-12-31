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
import com.sudox.messenger.android.moments.MomentVO
import kotlinx.android.synthetic.main.fragment_activity.activityContentList

class ActivityFragment : CoreFragment(), ViewPagerFragment, ApplicationBarListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val moments = ArrayList<MomentVO>()

        moments.add(MomentVO(
                "Your story", getDrawable(context!!, R.drawable.drawable_photo_3)!!, isCreatedByMe = true, isViewed = false)
        )

        moments.add(MomentVO(
                "Anton", getDrawable(context!!, R.drawable.drawable_photo_1)!!, isCreatedByMe = false, isViewed = false)
        )

        moments.add(MomentVO(
                "Yaroslav", getDrawable(context!!, R.drawable.drawable_photo_2)!!, isCreatedByMe = false, isViewed = true)
        )

        moments.add(MomentVO(
                "Yaroslav 2", getDrawable(context!!, R.drawable.drawable_photo_2)!!, isCreatedByMe = false, isViewed = true)
        )

        moments.add(MomentVO(
                "Yaroslav 3", getDrawable(context!!, R.drawable.drawable_photo_2)!!, isCreatedByMe = false, isViewed = true)
        )

        activityContentList.adapter = ActivityAdapter(context!!, moments)
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