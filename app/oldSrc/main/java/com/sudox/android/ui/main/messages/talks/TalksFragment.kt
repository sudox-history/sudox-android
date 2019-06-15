package com.sudox.android.ui.main.messages.talks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.design.tablayout.TabLayoutFragment
import javax.inject.Inject

class TalksFragment @Inject constructor(): TabLayoutFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_talks, container, false)
    }
}