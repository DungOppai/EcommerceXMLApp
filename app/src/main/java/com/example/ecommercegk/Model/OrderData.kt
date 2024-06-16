package com.example.ecommercegk.Model

import android.os.Parcel
import android.os.Parcelable

data class OrderData(
    val items: String = "",
    val quantity: Int = 0,
    val timestamp: String = "",
    val totalPrice: Double = 0.0,
    val address: String = "",
    val email: String = ""
)
