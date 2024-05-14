package com.example.przeterminarz

import android.os.Parcel
import android.os.Parcelable

data class Product(
    var id: Int,
    var name: String,
    var image: String,
    var category: Categories,
    var expirationDate: Long,
    var amount: Int,
    var state: States,
    var isDiscarded: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt() ?: 0,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Categories.valueOf(parcel.readString() ?: Categories.NO_CATEGORY.name),
        parcel.readLong() ?: 0,
        parcel.readInt() ?: 0,
        States.valueOf(parcel.readString() ?: States.UNKNOWN.name),
        parcel.readBoolean() ?: false
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(category.name)
        parcel.writeLong(expirationDate)
        parcel.writeInt(amount)
        parcel.writeString(state.name)
        parcel.writeBoolean(isDiscarded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
