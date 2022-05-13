package com.example.ipsatorpizzaapp.data.repositories

import com.example.ipsatorpizzaapp.data.remote.PizzaApi
import com.example.ipsatorpizzaapp.data.remote.responses.PizzaDto
import javax.inject.Inject

class PizzaRepository @Inject constructor(
    val api : PizzaApi
) {

    suspend fun getPizza():PizzaDto{
        return api.getRemotePizza()
    }
}