package com.example.vismaypatildemo.domain.model

import com.example.vismaypatildemo.domain.usecase.CalculatePortfolioSummaryUseCase
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PortfolioSummaryTest {

    private val calculateSummaryUseCase = CalculatePortfolioSummaryUseCase()

    private val sampleHoldings = listOf(
        Holding(
            symbol = "TCS",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 105.0
        ),
        Holding(
            symbol = "INFY",
            quantity = 5,
            ltp = 200.0,
            avgPrice = 180.0,
            close = 210.0
        )
    )

    @Test
    fun `calculateSummary creates PortfolioSummary with correct calculations`() {
        val summary = calculateSummaryUseCase(sampleHoldings)

        assertThat(summary.currentValue).isEqualTo(2000.0)
        assertThat(summary.totalInvestment).isEqualTo(1800.0)
        assertThat(summary.totalPnL).isEqualTo(200.0)
        assertThat(summary.todaysPnL).isEqualTo(100.0)
    }

    @Test
    fun `totalPnLPercentage is calculated correctly`() {
        val summary = calculateSummaryUseCase(sampleHoldings)

        assertThat(summary.totalPnLPercentage).isWithin(0.01).of(11.11)
    }

    @Test
    fun `isInProfit returns true when totalPnL is positive`() {
        val summary = calculateSummaryUseCase(sampleHoldings)

        assertThat(summary.isInProfit).isTrue()
    }

    @Test
    fun `isInProfit returns false when totalPnL is negative`() {
        val losingHoldings = listOf(
            Holding(
                symbol = "TCS",
                quantity = 10,
                ltp = 80.0,
                avgPrice = 100.0,
                close = 85.0
            )
        )

        val summary = calculateSummaryUseCase(losingHoldings)

        assertThat(summary.isInProfit).isFalse()
    }

    @Test
    fun `empty creates PortfolioSummary with zero values`() {
        val summary = PortfolioSummary.empty()

        assertThat(summary.holdings).isEmpty()
        assertThat(summary.currentValue).isEqualTo(0.0)
        assertThat(summary.totalInvestment).isEqualTo(0.0)
        assertThat(summary.totalPnL).isEqualTo(0.0)
        assertThat(summary.todaysPnL).isEqualTo(0.0)
    }

    @Test
    fun `totalPnLPercentage returns zero when investment is zero`() {
        val summary = PortfolioSummary.empty()

        assertThat(summary.totalPnLPercentage).isEqualTo(0.0)
    }
}