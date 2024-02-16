package com.example.mylove.Adapter

import android.os.Parcel
import android.os.Parcelable

data class PictureData(val id: Long, val image : String, val type: String, val about: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(image)
        parcel.writeString(type)
        parcel.writeString(about)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PictureData> {
        override fun createFromParcel(parcel: Parcel): PictureData {
            return PictureData(parcel)
        }

        override fun newArray(size: Int): Array<PictureData?> {
            return arrayOfNulls(size)
        }
    }
}
