package com.sudox.android.ui.main.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.os.Bundle
import android.support.constraint.motion.MotionLayout
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

    // Для анимации в блоке информации о профиле.
    private val startProfileNameTextSize = 20F
    private val endProfileNameTextSize = 15F
    private var closedProfileNameMeasuredHeight = 0
    private var closedProfileNameMeasuredWidth = 0

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

    private fun initToolbar() {
        profileToolbar.inflateMenu(R.menu.menu_profile)

        // Listen data
        profileViewModel.userLiveData.observe(this, Observer {
            profileAvatarView.bindUser(it!!)
            profileNameText.installText(it.name)

            // Show status only if it installed
            if (it.status != null) profileStatusText.text = it.status
        })

        // Animation will be configured before fragment will be drawen
        initProfileAnimation()
    }

    private fun initProfileAnimation() {
        val fontSizesRatio = Math.min(startProfileNameTextSize, endProfileNameTextSize) / Math.max(startProfileNameTextSize, endProfileNameTextSize)
        val startContraintSet = profileMotionLayout.getConstraintSet(R.id.scene_profile_start)
        val endContraintSet = profileMotionLayout.getConstraintSet(R.id.scene_profile_end)

        startContraintSet.setScaleX(R.id.profileNameText, 1F)
        startContraintSet.setScaleY(R.id.profileNameText, 1F)
        endContraintSet.setScaleX(R.id.profileNameText, fontSizesRatio)
        endContraintSet.setScaleY(R.id.profileNameText, fontSizesRatio)

        // Pivot, текст должен сжиматься налево, а не в центр
        profileNameText.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            profileNameText.pivotX = 0F
            profileNameText.pivotY = profileNameText
                    .measuredHeight
                    .toFloat()
        }
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