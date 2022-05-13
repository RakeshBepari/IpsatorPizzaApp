package com.example.ipsatorpizzaapp.data.remote.responses

data class PizzaDto(
    val id: String,
    val name: String,
    val isVeg: Boolean,
    val description: String,
    val defaultCrust: Int,
    val crusts: List<Crust>
)

fun getpizzadto():PizzaDto{
    return PizzaDto(
        crusts = listOf(),
        defaultCrust = 0,
        description = "",
        id = "0",
        isVeg = true,
        name = "Veg"
    )
}