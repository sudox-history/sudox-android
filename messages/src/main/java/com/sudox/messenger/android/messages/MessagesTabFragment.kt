package com.sudox.messenger.android.messages

import android.content.Context
import com.sudox.design.viewlist.ViewList
import com.sudox.messenger.android.core.fragments.ViewListFragment
import com.sudox.messenger.android.core.tabs.TabsChildFragment
import com.sudox.messenger.android.messages.adapters.DialogsAdapter

class MessagesTabFragment : ViewListFragment<DialogsAdapter>(), TabsChildFragment {

    var dialogsAdapter = DialogsAdapter()

    override fun getTitle(context: Context): String {
        return context.getString(R.string.messages)
    }

    override fun getAdapter(viewList: ViewList): DialogsAdapter? {
        return dialogsAdapter
    }

    override fun isAppBarConfiguredByRoot(): Boolean {
        return true
    }
}