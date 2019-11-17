package com.sudox.design.countriesProvider.entries

import android.content.Context
import android.os.Parcel
import android.os.Parcelable

class Country(
        val regionCode: String,
        val nameTextId: Int,
        val flagImageId: Int,
        val countryCode: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
            regionCode = parcel.readString()!!,
            nameTextId = parcel.readInt(),
            flagImageId = parcel.readInt(),
            countryCode = parcel.readInt()
    )

    fun getName(context: Context): String {
        return context.getString(nameTextId)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(regionCode)
        parcel.writeInt(nameTextId)
        parcel.writeInt(flagImageId)
        parcel.writeInt(countryCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Country> {
        override fun createFromParcel(parcel: Parcel): Country {
            return Country(parcel)
        }

        override fun newArray(size: Int): Array<Country?> {
            return arrayOfNulls(size)
        }
    }
}