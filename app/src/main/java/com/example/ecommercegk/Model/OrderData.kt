package com.example.ecommercegk.Model

class OrderData(
    var userId: String? = null,
    val productName: String? = null,
    val quantity: Int? = null,
    val process: String? = "on Delivering"
) {
}