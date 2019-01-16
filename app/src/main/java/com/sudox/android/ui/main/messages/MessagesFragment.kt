package com.sudox.android.ui.main.messages

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.android.ui.main.MainActivity
import com.sudox.android.ui.main.messages.channels.ChannelsFragment
import com.sudox.android.ui.main.messages.dialogs.DialogsFragment
import com.sudox.android.ui.main.messages.talks.TalksFragment
import com.sudox.design.adapters.TabLayoutAdapter
import com.sudox.design.navigation.NavigationRootFragment
import kotlinx.android.synthetic.main.activity_main.*
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

    private val mainActivity by lazy { activity as MainActivity }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()
        initViewPager()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_messages, container, false)
    }

    private fun initToolbar() {
        mainActivity.mainToolbar.reset()
        mainActivity.mainToolbar.setTitle(R.string.messages)
        mainActivity.mainToolbar.inflateMenu(R.menu.menu_messages)
    }

    private fun initViewPager() {
        val fragments = arrayOf<Fragment>(dialogsFragment, talksFragment, channelsFragment)
        val titles = arrayOf(getString(R.string.dialogs), getString(R.string.talks), getString(R.string.channels))
        val adapter = TabLayoutAdapter(fragments, titles, messagesViewPager, childFragmentManager)

        messagesViewPager.adapter = adapter
        messagesTabLayout.setupWithViewPager(messagesViewPager)
    }

    override fun onFragmentOpened() {
        if (activity != null) initToolbar()
    }

    override fun onFragmentClosed() {
    }
}