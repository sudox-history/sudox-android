package ru.sudox.design.viewlist

import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat.readBoolean
import androidx.core.os.ParcelCompat.writeBoolean
import androidx.recyclerview.widget.RecyclerView
import ru.sudox.design.viewlist.vos.ViewListHeaderVO

val EMPTY_STATE = object : ViewListState() {}

@Deprecated(message = "Replace by FlexibleAdapter")
open class ViewListState : Parcelable {

    var superState: Parcelable? = null
    var headersVOs: Array<ViewListHeaderVO>? = null
    var isLoadingEnabled = false

    constructor()
    constructor(superState: Parcelable) {
        this.superState = if (superState != EMPTY_STATE) {
            superState
        } else {
            null
        }
    }

    constructor(input: Parcel) : this(input.readParcelable(RecyclerView::class.java.classLoader) ?: EMPTY_STATE) {
        headersVOs = input.createTypedArray(ViewListHeaderVO.CREATOR)
        isLoadingEnabled = readBoolean(input)
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeParcelable(superState, flags)
        out.writeTypedArray(headersVOs, 0)
        writeBoolean(out, isLoadingEnabled)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun readFromView(view: ViewList) {
        (view.adapter as? ViewListAdapter<*>)?.let {
            it.saveNestedRecyclerViewsState()
            isLoadingEnabled = it.isLoadingEnabled
            headersVOs = it.headersVOs
        }
    }

    fun writeToView(view: ViewList) {
        (view.adapter as? ViewListAdapter<*>)?.let {
            it.isLoadingEnabled = isLoadingEnabled
            it.headersVOs = headersVOs
        }
    }

    companion object CREATOR : Parcelable.Creator<ViewListState> {
        override fun createFromParcel(parcel: Parcel): ViewListState {
            return ViewListState(parcel)
        }

        override fun newArray(size: Int): Array<ViewListState?> {
            return arrayOfNulls(size)
        }
    }
}