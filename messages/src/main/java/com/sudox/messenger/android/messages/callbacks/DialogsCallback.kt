package com.sudox.messenger.android.messages.callbacks

import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.messages.adapters.DialogsAdapter
import com.sudox.messenger.android.messages.vos.DialogItemViewVO
import com.sudox.messenger.android.messages.vos.DialogVO

class DialogsCallback(
        adapter: DialogsAdapter
) : ViewListCallback<DialogVO>(adapter) {

    override fun compare(first: DialogVO, second: DialogVO): Int {
        if (first.isViewed && !second.isViewed) {
            return 1
        }

        if (!first.isViewed && second.isViewed) {
            return -1
        }

        if (!first.isViewed && !second.isViewed) {
            if (first.isMuted && !second.isMuted) {
                return 1
            }

            if (!first.isMuted && second.isMuted) {
                return -1
            }
        }

        return -first.time.compareTo(second.time)
    }
}