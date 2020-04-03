package com.sudox.messenger.android.core.inject

import com.sudox.messenger.android.core.CoreFragment

interface CoreComponent {
    fun inject(coreFragment: CoreFragment)
}