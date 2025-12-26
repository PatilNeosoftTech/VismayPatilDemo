package com.example.vismaypatildemo.domain.model

data class Holding(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
) {
    val currentValue: Double get() = ltp * quantity
    val totalInvestment: Double get() = avgPrice * quantity
    val totalPnL: Double get() = currentValue - totalInvestment
    val todaysPnL: Double get() = (close - ltp) * quantity
    val pnLPercentage: Double get() = if (totalInvestment != 0.0) {
        (totalPnL / totalInvestment) * 100
    } else 0.0
}
