package com.sudox.android.ui.main.messages

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.common.di.viewmodels.getViewModel
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.common.BaseReconnectFragment
import com.sudox.android.ui.main.contacts.ContactsViewModel
import com.sudox.android.ui.main.messages.channels.ChannelsFragment
import com.sudox.android.ui.main.messages.dialogs.DialogsFragment
import com.sudox.android.ui.main.messages.talks.TalksFragment
import com.sudox.design.navigation.NavigationRootFragment
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main_messages.*
import javax.inject.Inject

class MessagesFragment @Inject constructor() : NavigationRootFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dialogsFragment: DialogsFragment

    @Inject
    lateinit var talksFragment: TalksFragment

    @Inject
    lateinit var channelsFragment: ChannelsFragment

    private lateinit var dialogsViewModel: ContactsViewModel
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogsViewModel = getViewModel(viewModelFactory)
        mainActivity = activity as MainActivity

        return inflater.inflate(R.layout.fragment_main_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        initViewPager()
    }

    private fun initToolbar() {
//        messagesToolbar.inflateMenu(R.menu.menu_messages)
//        messagesToolbar.setOnMenuItemClickListener {
//            when (it.itemId) {
//            }
//
//            return@setOnMenuItemClickListener true
//        }
    }

    private fun initViewPager() {
        messagesPager.adapter = MessagesAdapter(childFragmentManager)
        messagesPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(position: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> messagesAddFABButton.setImageResource(R.drawable.ic_add)
                    1 -> messagesAddFABButton.setImageResource(R.drawable.ic_add_talk)
                    2 -> messagesAddFABButton.setImageResource(R.drawable.ic_arrow_back)
                }

                // Если на прошлом экране кнопка была скрыта
                toggleFloatingActionButton(true)
            }

        })
        sliding_tabs.setupWithViewPager(messagesPager)
    }

    fun toggleFloatingActionButton(show: Boolean) {
        if (show) {
            messagesAddFABButton.show(true)
        } else {
            messagesAddFABButton.hide(true)
        }
    }

    override fun onFragmentOpened() {
    }

    override fun onFragmentClosed() {
    }

    private inner class MessagesAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> dialogsFragment
                1 -> talksFragment
                else -> channelsFragment
            }
        }

        override fun getCount(): Int = 3

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.dialogs_label)
                1 -> getString(R.string.talks_label)
                else -> getString(R.string.channels_label)
            }
        }
    }
}