package com.sudox.messenger.android.core.fragment

import androidx.fragment.app.Fragment

abstract class AppFragment : Fragment() {
    @AppFragmentType
    abstract fun getFragmentType(): Int
}