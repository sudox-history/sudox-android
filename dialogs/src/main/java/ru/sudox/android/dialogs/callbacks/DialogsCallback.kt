package ru.sudox.android.dialogs.callbacks

import ru.sudox.design.viewlist.ViewListCallback
import ru.sudox.android.dialogs.adapters.DialogsAdapter
import ru.sudox.android.dialogs.vos.DialogVO

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