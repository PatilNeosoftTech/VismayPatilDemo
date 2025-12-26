package com.example.vismaypatildemo.domain.model
data class PortfolioSummary(
    val holdings: List<Holding>,
    val currentValue: Double,
    val totalInvestment: Double,
    val totalPnL: Double,
    val todaysPnL: Double,
    val totalPnLPercentage: Double,
    val isInProfit: Boolean
) {

    companion object {
        fun empty(): PortfolioSummary = PortfolioSummary(
            holdings = emptyList(),
            currentValue = 0.0,
            totalInvestment = 0.0,
            totalPnL = 0.0,
            todaysPnL = 0.0,
            totalPnLPercentage = 0.0,
            isInProfit = false
        )
    }
}
