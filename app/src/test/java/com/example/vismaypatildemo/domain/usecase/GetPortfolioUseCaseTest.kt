package com.example.vismaypatildemo.domain.usecase

import app.cash.turbine.test
import com.example.vismaypatildemo.domain.model.Holding
import com.example.vismaypatildemo.domain.repository.PortfolioRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetPortfolioUseCaseTest {

    private lateinit var repository: PortfolioRepository
    private lateinit var calculateSummaryUseCase: CalculatePortfolioSummaryUseCase
    private lateinit var useCase: GetPortfolioUseCase

    private val sampleHoldings = listOf(
        Holding("TCS", 10, 100.0, 90.0, 105.0),
        Holding("INFY", 5, 200.0, 180.0, 210.0)
    )

    @Before
    fun setup() {
        repository = mockk()
        calculateSummaryUseCase = CalculatePortfolioSummaryUseCase()
        useCase = GetPortfolioUseCase(repository, calculateSummaryUseCase)
    }

    @Test
    fun `invoke returns PortfolioSummary with calculations`() = runTest {
        coEvery { repository.getHoldings(any()) } returns Result.success(sampleHoldings)

        val result = useCase()

        assertThat(result.isSuccess).isTrue()

        val summary = result.getOrNull()
        assertThat(summary?.holdings?.size).isEqualTo(2)
        assertThat(summary?.currentValue).isEqualTo(2000.0)
        assertThat(summary?.totalInvestment).isEqualTo(1800.0)
        assertThat(summary?.totalPnL).isEqualTo(200.0)
    }

    @Test
    fun `invoke with forceRefresh true calls repository with forceRefresh`() = runTest {
        coEvery { repository.getHoldings(true) } returns Result.success(sampleHoldings)

        useCase(forceRefresh = true)

        coVerify { repository.getHoldings(forceRefresh = true) }
    }

    @Test
    fun `invoke with forceRefresh false uses cache`() = runTest {
        coEvery { repository.getHoldings(false) } returns Result.success(sampleHoldings)

        useCase(forceRefresh = false)

        coVerify { repository.getHoldings(forceRefresh = false) }
    }

    @Test
    fun `invoke returns empty summary when no holdings`() = runTest {
        coEvery { repository.getHoldings(any()) } returns Result.success(emptyList())

        val result = useCase()

        assertThat(result.isSuccess).isTrue()

        val summary = result.getOrNull()
        assertThat(summary?.holdings).isEmpty()
        assertThat(summary?.currentValue).isEqualTo(0.0)
    }

    @Test
    fun `invoke correctly calculates portfolio summary`() = runTest {
        val holdings = listOf(
            Holding("STOCK1", 10, 50.0, 40.0, 55.0),
            Holding("STOCK2", 5, 100.0, 90.0, 105.0)
        )
        coEvery { repository.getHoldings(any()) } returns Result.success(holdings)

        val result = useCase()

        val summary = result.getOrNull()
        assertThat(summary?.currentValue).isEqualTo(1000.0)
        assertThat(summary?.totalInvestment).isEqualTo(850.0)
        assertThat(summary?.totalPnL).isEqualTo(150.0)
        assertThat(summary?.todaysPnL).isEqualTo(75.0)
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.getHoldings(any()) } returns Result.failure(exception)

        val result = useCase()

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `refresh calls repository refreshHoldings`() = runTest {
        coEvery { repository.refreshHoldings() } returns Result.success(Unit)

        val result = useCase.refresh()

        assertThat(result.isSuccess).isTrue()
        coVerify(exactly = 1) { repository.refreshHoldings() }
    }

    @Test
    fun `refresh returns failure when repository fails`() = runTest {
        val exception = Exception("Refresh failed")
        coEvery { repository.refreshHoldings() } returns Result.failure(exception)

        val result = useCase.refresh()

        assertThat(result.isFailure).isTrue()
    }
}