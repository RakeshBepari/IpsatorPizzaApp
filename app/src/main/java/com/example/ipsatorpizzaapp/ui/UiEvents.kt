package com.example.ipsatorpizzaapp.ui

import com.example.ipsatorpizzaapp.domain.CartItem

sealed class UiEvent() {
    data class Selected(val selectedCrust: String, val selectedSize: String) : UiEvent()
    data class AddItemToCart(val cartItem: CartItem) : UiEvent()
    data class AddQuantity(val cartItem: CartItem) : UiEvent()
    data class RemoveItemFromCart(val cartItem: CartItem) : UiEvent()
    data class ReduceQuantity(val cartItem: CartItem) : UiEvent()
}