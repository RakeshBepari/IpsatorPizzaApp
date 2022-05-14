package com.example.ipsatorpizzaapp.domain

data class CartItem(
    val name: String,
    val isVeg: Boolean,
    val description: String,
    val selectedCrust: String,
    val selectedSize: String,
    val price: Int,
    val quantity: Int
)
fun cartItem():CartItem{
    return CartItem(
        name = "",
        isVeg = false,
        description ="",
        selectedCrust = "",
        selectedSize = "",
        price = 0,
        quantity = 1
    )
}