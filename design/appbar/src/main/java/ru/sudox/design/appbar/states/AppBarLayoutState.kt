package ru.sudox.design.appbar.states

import android.os.Parcel
import android.os.Parcelable
import ru.sudox.design.appbar.AppBarLayout
import ru.sudox.design.saveableview.SaveableViewState

class AppBarLayoutState : SaveableViewState<AppBarLayout> {

    private var appBarId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        appBarId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(appBarId)
    }

    override fun readFromView(view: AppBarLayout) {
        appBarId = view.appBar!!.id
    }

    override fun writeToView(view: AppBarLayout) {
        view.appBar!!.id = appBarId
    }
}