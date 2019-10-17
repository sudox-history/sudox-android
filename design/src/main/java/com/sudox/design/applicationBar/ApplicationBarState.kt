package com.sudox.design.applicationBar

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class ApplicationBarState : View.BaseSavedState {

    private var buttonAtStartId = 0
    private var buttonAtEndId = 0
    private var titleTextId = 0

    constructor(superState: Parcelable) : super(superState)
    constructor(source: Parcel) : super(source) {
        buttonAtStartId = source.readInt()
        buttonAtEndId = source.readInt()
        titleTextId = source.readInt()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(buttonAtStartId)
        out.writeInt(buttonAtEndId)
        out.writeInt(titleTextId)
    }

    fun writeFromView(applicationBar: ApplicationBar) {
        buttonAtStartId = applicationBar.buttonAtStart!!.id
        buttonAtEndId = applicationBar.buttonAtEnd!!.id
        titleTextId = applicationBar.titleTextId
    }

    fun readToView(applicationBar: ApplicationBar) {
        applicationBar.buttonAtStart!!.id = buttonAtStartId
        applicationBar.buttonAtEnd!!.id = buttonAtEndId

        if (titleTextId != 0) {
            applicationBar.setTitle(titleTextId)
        }
    }

    companion object CREATOR : Parcelable.Creator<ApplicationBarState> {
        override fun createFromParcel(source: Parcel): ApplicationBarState {
            return ApplicationBarState(source)
        }

        override fun newArray(size: Int): Array<ApplicationBarState?> {
            return arrayOfNulls(size)
        }
    }
}