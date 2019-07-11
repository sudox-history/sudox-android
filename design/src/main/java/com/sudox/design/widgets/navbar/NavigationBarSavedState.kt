package com.sudox.design.widgets.navbar

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class NavigationBarSavedState : View.BaseSavedState {

    internal var buttonStartId: Int = 0
    internal var buttonsEndIds: IntArray? = null
    internal var titleTextRes: Int = 0
    internal var titleText: String? = null

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        readButtonsIds(source)
        readTitle(source)
    }

    private fun readButtonsIds(source: Parcel) {
        buttonStartId = source.readInt()

        val buttonsEndCount = source.readInt()
        buttonsEndIds = IntArray(buttonsEndCount)
        source.readIntArray(buttonsEndIds!!)
    }

    private fun readTitle(source: Parcel) {
        titleText = source.readString()
        titleTextRes = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)

        writeButtonsIds(out)
        writeTitle(out)
    }

    private fun writeTitle(out: Parcel) {
        out.writeString(titleText)
        out.writeInt(titleTextRes)
    }

    private fun writeButtonsIds(out: Parcel) {
        out.writeInt(buttonStartId)
        out.writeInt(buttonsEndIds!!.size)
        out.writeIntArray(buttonsEndIds)
    }

    fun readFromView(navigationBar: NavigationBar) {
        buttonStartId = navigationBar.buttonStart!!.id
        buttonsEndIds = IntArray(navigationBar.buttonsEnd.size) {
            navigationBar.buttonsEnd[it]!!.id
        }

        if (navigationBar.contentView == navigationBar.titleTextView) {
            if (navigationBar.titleTextRes != 0) {
                titleTextRes = navigationBar.titleTextRes
            } else {
                titleText = navigationBar.titleTextView.text.toString()
            }
        }
    }

    fun writeToView(navigationBar: NavigationBar) {
        navigationBar.buttonStart!!.id = buttonStartId

        for (i in 0 until buttonsEndIds!!.size) {
            navigationBar.buttonsEnd[i]!!.id = buttonsEndIds!![i]
        }

        if (titleTextRes != 0) {
            navigationBar.setTitleTextRes(titleTextRes)
        } else if (titleText != null) {
            navigationBar.setTitleText(titleText)
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