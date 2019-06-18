package com.sudox.design.widgets.navbar

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class NavigationBarSavedState : View.BaseSavedState {

    internal var buttonStartId: Int = 0
    internal var buttonsEndIds: IntArray? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        buttonStartId = source.readInt()

        val buttonsEndCount = source.readInt()
        buttonsEndIds = IntArray(buttonsEndCount)
        source.readIntArray(buttonsEndIds!!)
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.apply {
            writeInt(buttonStartId)
            writeInt(buttonsEndIds!!.size)
            writeIntArray(buttonsEndIds)
        }
    }

    fun readFromView(navigationBar: NavigationBar) {
        buttonStartId = navigationBar.buttonStart!!.id
        buttonsEndIds = IntArray(navigationBar.buttonsEnd.size) {
            navigationBar.buttonsEnd[it]!!.id
        }
    }

    fun writeToView(navigationBar: NavigationBar) {
        navigationBar.buttonStart!!.id = buttonStartId

        for (i in 0 until buttonsEndIds!!.size) {
            navigationBar.buttonsEnd[i]!!.id = buttonsEndIds!![i]
        }
    }

    companion object CREATOR : Parcelable.Creator<NavigationBarSavedState> {
        override fun createFromParcel(parcel: Parcel): NavigationBarSavedState {
            return NavigationBarSavedState(parcel)
        }

        override fun newArray(size: Int): Array<NavigationBarSavedState?> {
            return arrayOfNulls(size)
        }
    }
}