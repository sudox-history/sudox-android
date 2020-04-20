package ru.sudox.android.countries.vos

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * ViewObject страны
 *
 * @param regionCode Код региона (например: RU)
 * @param nameId ID названия страны
 * @param flagId ID флага
 * @param countryCode Код страны (например: 7)
 */
data class CountryVO(
        val regionCode: String,
        @StringRes val nameId: Int,
        @DrawableRes val flagId: Int,
        val countryCode: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()) {
    }

    fun getName(context: Context): String {
        return context.getString(nameId)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(regionCode)
        parcel.writeInt(nameId)
        parcel.writeInt(flagId)
        parcel.writeInt(countryCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CountryVO> {
        override fun createFromParcel(parcel: Parcel): CountryVO {
            return CountryVO(parcel)
        }

        override fun newArray(size: Int): Array<CountryVO?> {
            return arrayOfNulls(size)
        }
    }
}