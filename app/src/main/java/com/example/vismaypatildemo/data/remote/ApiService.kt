package com.example.vismaypatildemo.data.remote

import com.example.vismaypatildemo.data.remote.dto.HoldingsResponse
import retrofit2.http.GET

interface ApiService {
    @GET("/")
    suspend fun getHoldings(): HoldingsResponse
}