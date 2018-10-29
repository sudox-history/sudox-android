package com.sudox.android.data.models.chats

import android.os.Parcel
import android.os.Parcelable

class UserChatRecipient() : Parcelable {

    lateinit var uid: String
    lateinit var name: String
    lateinit var nickname: String
    lateinit var photo: String

    constructor(parcel: Parcel) : this() {
        uid = parcel.readString()
        name = parcel.readString()
        nickname = parcel.readString()
        photo = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeString(nickname)
        parcel.writeString(photo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserChatRecipient> {
        override fun createFromParcel(parcel: Parcel): UserChatRecipient {
            return UserChatRecipient(parcel)
        }

        override fun newArray(size: Int): Array<UserChatRecipient?> {
            return arrayOfNulls(size)
        }
    }


}