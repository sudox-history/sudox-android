package com.sudox.android.ui.main.messages.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ChannelsFragment @Inject constructor(): DaggerFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_channels, container, false)
    }
}