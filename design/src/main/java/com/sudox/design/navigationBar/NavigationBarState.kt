package com.sudox.design.navigationBar

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import com.sudox.design.navigationBar.navigationBarButton.NavigationBarButton

class NavigationBarState : View.BaseSavedState {

    private var buttonsIds: IntArray? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        buttonsIds = source.createIntArray()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeIntArray(buttonsIds)
    }

    fun writeFromView(navigationBar: NavigationBar) {
        buttonsIds = IntArray(navigationBar.buttons.size)

        navigationBar.buttons.forEachIndexed { index, button ->
            buttonsIds!![index] = button.id
        }
    }

    fun readToView(navigationBar: NavigationBar) = navigationBar.let { bar ->
        buttonsIds!!.forEach {
            bar.buttons.add(bar.createItem().apply {
                id = it
            })
        }
    }

    companion object CREATOR : Parcelable.Creator<NavigationBarState> {
        override fun createFromParcel(source: Parcel): NavigationBarState {
            return NavigationBarState(source)
        }

        override fun newArray(size: Int): Array<NavigationBarState?> {
            return arrayOfNulls(size)
        }
    }
}