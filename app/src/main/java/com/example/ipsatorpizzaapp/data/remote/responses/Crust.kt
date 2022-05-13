package com.example.ipsatorpizzaapp.data.remote.responses

data class Crust(
    val id: Int,
    val name: String,
    val defaultSize: Int,
    val sizes: List<Size>
)