package com.empire.sente.userAuthentication.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    val userId: String?,
    val firstName: String?,
    val lastName: String?,
    val birthMonth: String?,
    val birthDay: String?,
    val birthYear: String?,
    val gender: String?,
    val phoneNumber: String?
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(birthMonth)
        parcel.writeString(birthDay)
        parcel.writeString(birthYear)
        parcel.writeString(gender)
        parcel.writeString(phoneNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}
