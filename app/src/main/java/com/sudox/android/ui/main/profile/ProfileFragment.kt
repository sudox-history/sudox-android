package com.sudox.android.ui.main.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.content.res.Configuration
import android.content.res.Configuration.*
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.profile.decorations.ProfileDecorationsFragment
import com.sudox.android.ui.main.profile.info.ProfileInfoFragment
import com.sudox.design.adapters.TabLayoutAdapter
import com.sudox.design.navigation.NavigationRootFragment
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main_profile.*
import javax.inject.Inject

class ProfileFragment @Inject constructor() : NavigationRootFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var profileInfoFragment: ProfileInfoFragment

    @Inject
    lateinit var profileDecorationsFragment: ProfileDecorationsFragment

    private val profileViewModel by lazy { getViewModel<ProfileViewModel>(viewModelFactory) }
    private val mainActivity by lazy { activity as MainActivity }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Configure view
        initToolbar()
        initViewPager()

        profileViewModel.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main_profile, container, false)
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

        // Listen data
        profileViewModel.userLiveData.observe(this, Observer {
            profileAvatarView.bindUser(it!!)
            profileNameText.text = it.name
            profileStatusText.text = it.status
        })
    }

    override fun onFragmentOpened() {
        // Переводим телефон в портретную ориентацию и блокируем автоповорот
        if (mainActivity.requestedOrientation != SCREEN_ORIENTATION_PORTRAIT) {
            mainActivity.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onFragmentClosed() {
        // Разблокируем автоповорот
        mainActivity.requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
    }
}