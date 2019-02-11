package com.sudox.android.ui.main.profile.decorations

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudox.android.R
import com.sudox.design.tablayout.TabLayoutFragment
import javax.inject.Inject

class ProfileDecorationsFragment @Inject constructor() : TabLayoutFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profile_decorations, container, false)
    }
}