package ru.sudox.android.auth.views

import android.os.Parcel
import android.os.Parcelable
import ru.sudox.design.saveableview.SaveableViewState

class AuthScreenLayoutState : SaveableViewState<AuthScreenLayout> {

    private var childIds: IntArray? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        childIds = source.createIntArray()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeIntArray(childIds)
    }

    override fun readFromView(view: AuthScreenLayout) {
        childIds = IntArray(view.childViews?.size ?: 0) {
            view.childViews!![it].id
        }
    }

    override fun writeToView(view: AuthScreenLayout) {
        childIds?.forEachIndexed { index, id ->
            view.childViews!![index].id = id
        }
    }
}