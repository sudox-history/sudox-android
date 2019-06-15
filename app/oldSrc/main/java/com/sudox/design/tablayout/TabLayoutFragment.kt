package com.sudox.design.tablayout

import android.content.Context
import dagger.android.support.DaggerFragment

abstract class TabLayoutFragment : DaggerFragment() {

    private var wasVisible = false
    private var sendOnAttach: Boolean = false

    open fun onFirstVisible() {}
    fun onSelected() {
        if (wasVisible) return
        if (!isAdded) {
            sendOnAttach = true
            wasVisible = true
            return
        }

        wasVisible = true
        onFirstVisible()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (sendOnAttach) {
            onFirstVisible()
            sendOnAttach = false
        }
    }
}