package ru.sudox.android.layouts

import android.os.Parcel
import android.os.Parcelable
import ru.sudox.design.saveableview.SaveableViewState

class AppLayoutState : SaveableViewState<AppLayout> {

    private var contentLayoutId = 0
    private var navigationBarId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        contentLayoutId = source.readInt()
        navigationBarId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(navigationBarId)
    }

    override fun readFromView(view: AppLayout) {
        contentLayoutId = view.contentLayout.id
        navigationBarId = view.bottomNavigationView.id
    }

    override fun writeToView(view: AppLayout) {
        view.contentLayout.id = contentLayoutId
        view.bottomNavigationView.id = navigationBarId
    }
}