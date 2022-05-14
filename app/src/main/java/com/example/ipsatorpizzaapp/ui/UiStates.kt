package com.example.ipsatorpizzaapp.ui

import com.example.ipsatorpizzaapp.data.remote.responses.PizzaDto
import com.example.ipsatorpizzaapp.data.remote.responses.getpizzadto
import com.example.ipsatorpizzaapp.domain.CartItem

data class PizzaUiState(
    val pizzaDto: PizzaDto = getpizzadto(),
    var crustSizeMap: MutableMap<String, List<String>> = mutableMapOf(),
    var currentPrice: Int = 235,
    var cartItemList: MutableList<CartItem> = mutableListOf(),
    val totalQuantity: Int = 0,
    val totalPrice: Int = 0

)