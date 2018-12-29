package com.sudox.design.navigation

import dagger.android.support.DaggerFragment

abstract class NavigationRootFragment : DaggerFragment() {

    abstract fun onFragmentOpened()
    abstract fun onFragmentClosed()
}