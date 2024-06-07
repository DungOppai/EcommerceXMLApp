package com.example.ecommercegk.Model

import android.os.Parcel
import android.os.Parcelable

data class OrderData(
    val items: String = "" ,
    val totalPrice: Double = 0.0,
    val quantity: Int = 0,
    val timestamp: String = ""
)
: Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString().toString(),

        )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(items)
        dest.writeDouble(totalPrice)
        dest.writeInt(quantity)
        dest.writeString(timestamp)
    }

    companion object CREATOR : Parcelable.Creator<OrderData> {
        override fun createFromParcel(parcel: Parcel): OrderData {
            return OrderData(parcel)
        }

        override fun newArray(size: Int): Array<OrderData?> {
            return arrayOfNulls(size)
        }
    }
}