package com.example.vismaypatildemo.domain.usecase

import com.example.vismaypatildemo.domain.model.Holding
import com.example.vismaypatildemo.domain.model.PortfolioSummary
import javax.inject.Inject

class CalculatePortfolioSummaryUseCase @Inject constructor() {

    operator fun invoke(holdings: List<Holding>): PortfolioSummary {
        if (holdings.isEmpty()) {
            return PortfolioSummary.empty()
        }

        val currentValue = holdings.sumOf { it.currentValue }
        val totalInvestment = holdings.sumOf { it.totalInvestment }
        val totalPnL = currentValue - totalInvestment
        val todaysPnL = holdings.sumOf { it.todaysPnL }
        val totalPnLPercentage: Double = if (totalInvestment != 0.0) {
            (totalPnL / totalInvestment) * 100
        } else 0.0

        val isInProfit: Boolean = totalPnL >= 0

        return PortfolioSummary(
            holdings = holdings,
            currentValue = currentValue,
            totalInvestment = totalInvestment,
            totalPnL = totalPnL,
            todaysPnL = todaysPnL,
            totalPnLPercentage = totalPnLPercentage,
            isInProfit = isInProfit
        )
    }
}