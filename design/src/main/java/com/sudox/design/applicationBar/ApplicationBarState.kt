package com.sudox.design.applicationBar

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class ApplicationBarState : View.BaseSavedState {

    private var buttonStartId: Int = 0
    private var buttonsEndIds: IntArray? = null
    private var titleTextRes: Int = 0
    private var titleText: String? = null

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

    fun writeFromView(navigationBar: ApplicationBar) {
        buttonStartId = navigationBar.buttonAtStart!!.id
        buttonsEndIds = IntArray(navigationBar.buttonsAtEnd.size) {
            navigationBar.buttonsAtEnd[it]!!.id
        }

        if (navigationBar.contentView == navigationBar.titleTextView) {
            if (navigationBar.titleTextRes != 0) {
                titleTextRes = navigationBar.titleTextRes
            } else {
                titleText = navigationBar.titleTextView.text.toString()
            }
        }
    }

    fun readToView(navigationBar: ApplicationBar) {
        navigationBar.buttonAtStart!!.id = buttonStartId

        for (i in buttonsEndIds!!.indices) {
            navigationBar.buttonsAtEnd[i]!!.id = buttonsEndIds!![i]
        }

        if (titleTextRes != 0) {
            navigationBar.setTitleText(titleTextRes)
        } else if (titleText != null) {
            navigationBar.setTitleText(titleText)
        }
    }

    companion object CREATOR : Parcelable.Creator<ApplicationBarState> {
        override fun createFromParcel(parcel: Parcel): ApplicationBarState {
            return ApplicationBarState(parcel)
        }

        override fun newArray(size: Int): Array<ApplicationBarState?> {
            return arrayOfNulls(size)
        }
    }
}