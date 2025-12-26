package com.example.vismaypatildemo.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HoldingTest {

    @Test
    fun `currentValue is calculated correctly`() {
        val holding = Holding(
            symbol = "TCS",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 105.0
        )

        val currentValue = holding.currentValue

    }

    @Test
    fun `totalInvestment is calculated correctly`() {
        val holding = Holding(
            symbol = "TCS",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 105.0
        )

        val totalInvestment = holding.totalInvestment

    }

    @Test
    fun `totalPnL is calculated correctly for profit`() {
        val holding = Holding(
            symbol = "TCS",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 105.0
        )

        val totalPnL = holding.totalPnL

    }

    @Test
    fun `totalPnL is calculated correctly for loss`() {
        val holding = Holding(
            symbol = "TCS",
            quantity = 10,
            ltp = 80.0,
            avgPrice = 90.0,
            close = 85.0
        )

        val totalPnL = holding.totalPnL

    }

    @Test
    fun `todaysPnL is calculated correctly`() {
        val holding = Holding(
            symbol = "TCS",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 105.0
        )

        val todaysPnL = holding.todaysPnL

    }

    @Test
    fun `pnLPercentage is calculated correctly for profit`() {
        val holding = Holding(
            symbol = "TCS",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 105.0
        )

        val pnLPercentage = holding.pnLPercentage

    }

    @Test
    fun `pnLPercentage returns zero when investment is zero`() {
        val holding = Holding(
            symbol = "TCS",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 0.0,
            close = 105.0
        )

        val pnLPercentage = holding.pnLPercentage

        assertThat(pnLPercentage).isEqualTo(0.0)
    }
}