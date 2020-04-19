package ru.sudox.design.appbar.states

import android.os.Parcel
import android.os.Parcelable
import ru.sudox.design.appbar.AppBar
import ru.sudox.design.saveableview.SaveableViewState

class AppBarState : SaveableViewState<AppBar> {

    private var viewAtLeftId = 0
    private var viewAtRightId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        viewAtLeftId = source.readInt()
        viewAtRightId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(viewAtLeftId)
        out.writeInt(viewAtRightId)
    }

    override fun readFromView(view: AppBar) {
        viewAtLeftId = view.viewAtLeft?.id ?: 0
        viewAtRightId = view.viewAtRight?.id ?: 0
    }

    override fun writeToView(view: AppBar) {
        view.viewAtLeft?.id = viewAtLeftId
        view.viewAtRight?.id = viewAtRightId
    }
}