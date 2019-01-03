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
    private val startProfileBlockHeight by lazy { 172F * mainActivity.resources.displayMetrics.density }
    private val endProfileBlockHeight by lazy { 65F * mainActivity.resources.displayMetrics.density }
    private val profileBlockHeightsDiff by lazy { Math.max(startProfileBlockHeight, endProfileBlockHeight) - Math.min(startProfileBlockHeight, endProfileBlockHeight) }

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
            profileStatusText.installText(if (it.status != null) {
                it.status!!
            } else {
                getString(R.string.im_using_sudox)
            })
        })

        // Animation will be configured before fragment will be drawen
        initProfileAnimation()
    }

    private fun initProfileAnimation() {
        val fontSizesRatio = Math.min(startProfileNameTextSize, endProfileNameTextSize) / Math.max(startProfileNameTextSize, endProfileNameTextSize)
        val startConstraintSet = profileMotionLayout.getConstraintSet(R.id.scene_profile_start)
        val endConstraintSet = profileMotionLayout.getConstraintSet(R.id.scene_profile_end)

        startConstraintSet.setScaleX(R.id.profileNameText, 1F)
        startConstraintSet.setScaleY(R.id.profileNameText, 1F)
        endConstraintSet.setScaleX(R.id.profileNameText, fontSizesRatio)
        endConstraintSet.setScaleY(R.id.profileNameText, fontSizesRatio)

        // Pivot, текст должен сжиматься налево, а не в центр
        profileNameText.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            profileNameText.pivotX = 0F
            profileNameText.pivotY = profileNameText
                    .measuredHeight
                    .toFloat() / 2
        }

        profileMotionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}
            override fun onTransitionChange(p0: MotionLayout, p1: Int, p2: Int, p3: Float) {
                if (p2 == R.id.scene_profile_end) {
                    val newHeight = (startProfileBlockHeight - (profileBlockHeightsDiff * p3)).toInt()

                    // Уменьшение
                    if (newHeight != p0.layoutParams.height) {
                        p0.layoutParams = p0.layoutParams.apply { height = newHeight }
                    }
                } else if (p2 == R.id.scene_profile_start) {
                    val newHeight = (endProfileBlockHeight + (profileBlockHeightsDiff * p3)).toInt()

                    // Увеличение
                    if (newHeight != p0.layoutParams.height) {
                        p0.layoutParams = p0.layoutParams.apply { height = newHeight }
                    }
                }
            }
        })

        // Прокрутка текстов
        profileNameText.isSelected = true
        profileStatusText.isSelected = true

        // Ставим в начальное положение
        profileMotionLayout.layoutParams = profileMotionLayout
                .layoutParams
                .apply { height = startProfileBlockHeight.toInt() }

        // Связываем верстку с анимацией
        profileConstraintLayout.motionLayout = profileMotionLayout
        profileConstraintLayout.maxScrollY = startProfileBlockHeight.toInt()
    }

    private fun initViewPager() {
        val fragments = arrayOf(profileInfoFragment, profileDecorationsFragment)
        val titles = arrayOf(getString(R.string.profile_info), getString(R.string.decorations_label))

        profileViewPager.adapter = TabLayoutAdapter(fragments, titles, profileViewPager, childFragmentManager)
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