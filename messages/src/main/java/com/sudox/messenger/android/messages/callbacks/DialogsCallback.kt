package com.sudox.messenger.android.messages.callbacks

import com.sudox.design.viewlist.ViewListCallback
import com.sudox.messenger.android.messages.adapters.DialogsAdapter
import com.sudox.messenger.android.messages.vos.DialogVO

class DialogsCallback(
        adapter: DialogsAdapter
) : ViewListCallback<DialogVO>(adapter) {

    override fun compare(first: DialogVO, second: DialogVO): Int {
        if (first.isViewedByMe && !second.isViewedByMe) {
            return 1
        }

        if (!first.isViewedByMe && second.isViewedByMe) {
            return -1
        }

        if (!first.isViewedByMe && !second.isViewedByMe) {
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