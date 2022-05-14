package com.example.ipsatorpizzaapp.data.repositories

import com.example.ipsatorpizzaapp.data.remote.PizzaApi
import com.example.ipsatorpizzaapp.data.remote.responses.PizzaDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PizzaRepository @Inject constructor(
    private val api : PizzaApi
) {

    suspend fun getPizza():PizzaDto{

        return withContext(Dispatchers.IO){
             api.getRemotePizza()
        }

    }
}