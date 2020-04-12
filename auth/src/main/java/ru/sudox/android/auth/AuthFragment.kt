package ru.sudox.android.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import ru.sudox.android.auth.views.AuthScreenLayout
import ru.sudox.android.auth.vos.AuthScreenVO
import ru.sudox.android.core.CoreFragment

const val SCROLL_VIEW_ID = "scroll_view_id"
const val AUTH_FRAGMENT_LAYOUT_ID_KEY = "auth_fragment_layout_id"

open class AuthFragment<T : AuthScreenVO> : CoreFragment() {

    var screenVO: T? = null
    var cachedView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (cachedView != null) {
            return cachedView
        }

        cachedView = ScrollView(context).apply {
            id = savedInstanceState?.getInt(SCROLL_VIEW_ID, View.generateViewId()) ?: View.generateViewId()

            addView(AuthScreenLayout(context!!).apply {
                id = savedInstanceState?.getInt(AUTH_FRAGMENT_LAYOUT_ID_KEY, View.generateViewId()) ?: View.generateViewId()
                vo = screenVO
            })
        }

        return cachedView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (view != null) {
            outState.putInt(SCROLL_VIEW_ID, view!!.id)
            outState.putInt(AUTH_FRAGMENT_LAYOUT_ID_KEY, (view as ViewGroup).getChildAt(0).id)
        }
    }
}