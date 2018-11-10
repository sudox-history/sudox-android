package com.sudox.android.ui.main.profile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.ui.main.common.BaseReconnectFragment
import com.sudox.android.ui.main.profile.decorations.ProfileDecorationsFragment
import com.sudox.android.ui.main.profile.info.ProfileInfoFragment
import com.sudox.android.ui.main.profile.info.adapter.ProfileParametersAdapter
import kotlinx.android.synthetic.main.fragment_main_profile.*
import javax.inject.Inject

class ProfileFragment @Inject constructor() : BaseReconnectFragment() {

    @Inject
    lateinit var profileParametersAdapter: ProfileParametersAdapter

    @Inject
    lateinit var profileInfoFragment: ProfileInfoFragment

    @Inject
    lateinit var profileDecorationsFragment: ProfileDecorationsFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewPager()
    }

    private fun initViewPager() {
        profilePager.adapter = ProfileAdapter(childFragmentManager)
        profileTabLayout.setupWithViewPager(profilePager)
    }

    override fun showConnectionStatus(isConnect: Boolean) {

    }

    private inner class ProfileAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> profileInfoFragment
                else -> profileDecorationsFragment
            }
        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence {
            return when(position) {
                0 -> getString(R.string.info_label)
                else -> getString(R.string.decoration_label)
            }
        }
    }
}