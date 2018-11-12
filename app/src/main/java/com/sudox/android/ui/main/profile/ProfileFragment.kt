package com.sudox.android.ui.main.profile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.data.database.model.User
import com.sudox.android.data.models.avatar.AvatarInfo
import com.sudox.android.data.models.avatar.impl.ColorAvatarInfo
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.common.BaseReconnectFragment
import com.sudox.android.ui.main.profile.decorations.ProfileDecorationsFragment
import com.sudox.android.ui.main.profile.info.ProfileInfoFragment
import com.sudox.design.helpers.drawAvatar
import com.sudox.design.helpers.drawCircleBitmap
import com.sudox.design.helpers.getTwoFirstLetters
import kotlinx.android.synthetic.main.fragment_main_profile.*
import javax.inject.Inject

class ProfileFragment @Inject constructor() : BaseReconnectFragment() {

    @Inject
    lateinit var profileInfoFragment: ProfileInfoFragment

    @Inject
    lateinit var profileDecorationsFragment: ProfileDecorationsFragment


    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mainActivity = activity as MainActivity

        listenForConnection()
        return inflater.inflate(R.layout.fragment_main_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initTopLayer()
        initViewPager()
    }

    private fun initTopLayer() {

        val user = User().apply {
            avatar = "col.f093fb.f5576c"
            name = "Максим Митюшкин"
        }

        val avatarInfo = AvatarInfo.parse(user.avatar)

        if (avatarInfo is ColorAvatarInfo) {
            drawCircleBitmap(context!!, drawAvatar(
                    text = user.name.getTwoFirstLetters(),
                    firstColor = avatarInfo.firstColor,
                    secondColor = avatarInfo.secondColor), profileAvatar)
        }
    }

    private fun initViewPager() {
        profilePager.adapter = ProfileAdapter(childFragmentManager)
        profileTabLayout.setupWithViewPager(profilePager)
    }

    override fun showConnectionStatus(isConnect: Boolean) {
        if (isConnect) {
            profileToolbar.title = getString(R.string.profile)
        } else {
            profileToolbar.title = getString(R.string.wait_for_connect)
        }
    }

    private inner class ProfileAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> profileInfoFragment
                else -> profileDecorationsFragment
            }
        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> getString(R.string.info_label)
                else -> getString(R.string.decoration_label)
            }
        }
    }
}