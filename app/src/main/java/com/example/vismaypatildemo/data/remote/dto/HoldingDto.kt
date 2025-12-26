package com.example.vismaypatildemo.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HoldingsResponse(
    @SerialName("data")
    val data: UserHoldingData
)

@Serializable
data class UserHoldingData(
    @SerialName("userHolding")
    val userHolding: List<HoldingDto>
)

@Serializable
data class HoldingDto(
    @SerialName("symbol")
    val symbol: String,

    @SerialName("quantity")
    val quantity: Int,

    @SerialName("ltp")
    val ltp: Double,

    @SerialName("avgPrice")
    val avgPrice: Double,

    @SerialName("close")
    val close: Double
)