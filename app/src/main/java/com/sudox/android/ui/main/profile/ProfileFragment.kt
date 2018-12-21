package com.sudox.android.ui.main.profile

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.ui.main.profile.decorations.ProfileDecorationsFragment
import com.sudox.android.ui.main.profile.info.ProfileInfoFragment
import com.sudox.design.adapters.TabLayoutAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main_profile.*
import javax.inject.Inject

class ProfileFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var profileInfoFragment: ProfileInfoFragment

    @Inject
    lateinit var profileDecorationsFragment: ProfileDecorationsFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        profileViewModel = getViewModel(viewModelFactory)

        return inflater.inflate(R.layout.fragment_main_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar()
        initViewPager()

        // Super!
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViewPager() {
        profileViewPager.adapter = TabLayoutAdapter(
                arrayOf(profileInfoFragment, profileDecorationsFragment),
                arrayOf(getString(R.string.profile_info), getString(R.string.decorations_label)),
                profileViewPager,
                childFragmentManager
        )

        // Connect with adapter
        profileTabLayout.setupWithViewPager(profileViewPager)
    }

    private fun initToolbar() {
        profileToolbar.inflateMenu(R.menu.menu_profile)
    }
}