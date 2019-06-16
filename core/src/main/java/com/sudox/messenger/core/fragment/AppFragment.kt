package com.sudox.messenger.core.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

internal const val PARAMS_KEY = "params"

abstract class AppFragment : Fragment() {
    var params: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null && savedInstanceState.containsKey(PARAMS_KEY)) {
            params = savedInstanceState.getBundle(PARAMS_KEY)
        }

        if (params != null) {
            onParamsReady()
            params = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (params != null) {
            outState.putBundle(PARAMS_KEY, params)
        }
    }

    abstract fun onParamsReady()

    @AppFragmentType
    abstract fun getFragmentType(): Int
}