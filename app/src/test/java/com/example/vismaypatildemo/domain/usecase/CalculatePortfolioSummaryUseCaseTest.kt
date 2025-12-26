package com.example.vismaypatildemo.domain.usecase

import com.example.vismaypatildemo.domain.model.Holding
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CalculatePortfolioSummaryUseCaseTest {

    private val useCase = CalculatePortfolioSummaryUseCase()

    @Test
    fun `invoke calculates summary correctly for multiple holdings`() {
        val holdings = listOf(
            Holding("TCS", 10, 100.0, 90.0, 105.0),
            Holding("INFY", 5, 200.0, 180.0, 210.0)
        )

        val summary = useCase(holdings)

        assertThat(summary.holdings).isEqualTo(holdings)
        assertThat(summary.currentValue).isEqualTo(2000.0)
        assertThat(summary.totalInvestment).isEqualTo(1800.0)
        assertThat(summary.totalPnL).isEqualTo(200.0)
        assertThat(summary.todaysPnL).isEqualTo(100.0)
    }

    @Test
    fun `invoke returns empty summary for empty holdings list`() {
        val summary = useCase(emptyList())

        assertThat(summary.holdings).isEmpty()
        assertThat(summary.currentValue).isEqualTo(0.0)
        assertThat(summary.totalInvestment).isEqualTo(0.0)
        assertThat(summary.totalPnL).isEqualTo(0.0)
        assertThat(summary.todaysPnL).isEqualTo(0.0)
    }

    @Test
    fun `invoke calculates single holding correctly`() {
        val holding = Holding("TCS", 10, 100.0, 90.0, 105.0)
        val holdings = listOf(holding)

        val summary = useCase(holdings)

        assertThat(summary.currentValue).isEqualTo(1000.0)
        assertThat(summary.totalInvestment).isEqualTo(900.0)
        assertThat(summary.totalPnL).isEqualTo(100.0)
        assertThat(summary.todaysPnL).isEqualTo(50.0)
    }

    @Test
    fun `invoke handles negative PnL correctly`() {
        val holdings = listOf(
            Holding("TCS", 10, 80.0, 100.0, 85.0)
        )

        val summary = useCase(holdings)

        assertThat(summary.totalPnL).isEqualTo(-200.0)
        assertThat(summary.isInProfit).isFalse()
    }
}