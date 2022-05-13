package com.example.ipsatorpizzaapp.data.remote

import com.example.ipsatorpizzaapp.data.remote.responses.PizzaDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PizzaApi {

    @GET("{api}/v1/pizza/1")
    suspend fun getRemotePizza(
        @Path("api") api: String = "api"
    ): PizzaDto

}