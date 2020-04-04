package com.sudox.messenger.android.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.sudox.messenger.android.auth.views.AuthScreenLayout
import com.sudox.messenger.android.auth.vos.AuthScreenVO
import com.sudox.messenger.android.core.CoreFragment

const val SCROLL_VIEW_ID = "scroll_view_id"
const val AUTH_FRAGMENT_LAYOUT_ID_KEY = "auth_fragment_layout_id"

open class AuthFragment<T : AuthScreenVO> : CoreFragment() {

    var screenVO: T? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ScrollView(context).apply {
            id = savedInstanceState?.getInt(SCROLL_VIEW_ID, View.generateViewId()) ?: View.generateViewId()

            addView(AuthScreenLayout(context!!).apply {
                id = savedInstanceState?.getInt(AUTH_FRAGMENT_LAYOUT_ID_KEY, View.generateViewId()) ?: View.generateViewId()
                vo = screenVO
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (view != null) {
            outState.putInt(SCROLL_VIEW_ID, view!!.id)
            outState.putInt(AUTH_FRAGMENT_LAYOUT_ID_KEY, (view as ViewGroup).getChildAt(0).id)
        }
    }
}