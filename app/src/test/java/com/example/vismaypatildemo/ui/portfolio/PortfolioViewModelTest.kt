package com.example.vismaypatildemo.ui.portfolio

import app.cash.turbine.test
import com.example.vismaypatildemo.domain.model.Holding
import com.example.vismaypatildemo.domain.usecase.GetPortfolioUseCase
import com.example.vismaypatildemo.core.StringResourceProvider
import com.example.vismaypatildemo.domain.usecase.CalculatePortfolioSummaryUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    private lateinit var useCase: GetPortfolioUseCase
    private lateinit var stringResourceProvider: StringResourceProvider
    private lateinit var viewModel: PortfolioViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val sampleHoldings = listOf(
        Holding("TCS", 10, 100.0, 90.0, 105.0),
        Holding("INFY", 5, 200.0, 180.0, 210.0)
    )
    private val calculateSummaryUseCase = CalculatePortfolioSummaryUseCase()
    private val sampleSummary = calculateSummaryUseCase(sampleHoldings)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = mockk()
        stringResourceProvider = mockk(relaxed = true)
        every { stringResourceProvider.getString(any()) } returns "Test string"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        coEvery { useCase(any()) } returns Result.success(sampleSummary)

        viewModel = PortfolioViewModel(useCase, stringResourceProvider)

        val initialState = viewModel.uiState.value
        assertThat(initialState).isInstanceOf(PortfolioUiState.Loading::class.java)
    }

    @Test
    fun `state updates to Success when data loads successfully`() = runTest {
        coEvery { useCase(any()) } returns Result.success(sampleSummary)

        viewModel = PortfolioViewModel(useCase, stringResourceProvider)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(PortfolioUiState.Success::class.java)

        val successState = state as PortfolioUiState.Success
        assertThat(successState.portfolio.holdings.size).isEqualTo(2)
        assertThat(successState.isSummaryExpanded).isFalse()
    }

    @Test
    fun `state updates to Error when data loading fails`() = runTest {
        val errorMessage = "Network error"
        coEvery { useCase(any()) } returns Result.failure(Exception(errorMessage))

        viewModel = PortfolioViewModel(useCase, stringResourceProvider)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(PortfolioUiState.Error::class.java)

        val errorState = state as PortfolioUiState.Error
        assertThat(errorState.message).isEqualTo(errorMessage)
    }

    @Test
    fun `toggleSummaryExpansion toggles expansion state`() = runTest {
        coEvery { useCase(any()) } returns Result.success(sampleSummary)

        viewModel = PortfolioViewModel(useCase, stringResourceProvider)
        advanceUntilIdle()

        viewModel.uiState.test {
            val initialState = awaitItem() as PortfolioUiState.Success
            assertThat(initialState.isSummaryExpanded).isFalse()

            viewModel.toggleSummaryExpansion()

            val toggledState = awaitItem() as PortfolioUiState.Success
            assertThat(toggledState.isSummaryExpanded).isTrue()

            viewModel.toggleSummaryExpansion()

            val toggledBackState = awaitItem() as PortfolioUiState.Success
            assertThat(toggledBackState.isSummaryExpanded).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshPortfolio calls useCase refresh and reloads data`() = runTest {
        coEvery { useCase(any()) } returns Result.success(sampleSummary)
        coEvery { useCase.refresh() } returns Result.success(Unit)

        viewModel = PortfolioViewModel(useCase, stringResourceProvider)
        advanceUntilIdle()

        viewModel.refreshPortfolio()
        advanceUntilIdle()

        coVerify(exactly = 1) { useCase.refresh() }
    }

    @Test
    fun `retry calls loadPortfolio again`() = runTest {
        coEvery { useCase(any()) } returns
                Result.failure(Exception("Error")) andThen
                Result.success(sampleSummary)

        viewModel = PortfolioViewModel(useCase, stringResourceProvider)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isInstanceOf(PortfolioUiState.Error::class.java)

        viewModel.retry()
        advanceUntilIdle()

        coVerify(exactly = 2) { useCase(forceRefresh = true) }
        assertThat(viewModel.uiState.value).isInstanceOf(PortfolioUiState.Success::class.java)
    }

    @Test
    fun `toggleSummaryExpansion does nothing when state is Error`() = runTest {
        coEvery { useCase(any()) } returns Result.failure(Exception("Error"))

        viewModel = PortfolioViewModel(useCase, stringResourceProvider)
        advanceUntilIdle()

        val initialState = viewModel.uiState.value
        assertThat(initialState).isInstanceOf(PortfolioUiState.Error::class.java)

        viewModel.toggleSummaryExpansion()
        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertThat(finalState).isEqualTo(initialState)
    }

    @Test
    fun `loadPortfolio is called with forceRefresh true on initialization`() = runTest {
        coEvery { useCase(any()) } returns Result.success(sampleSummary)

        viewModel = PortfolioViewModel(useCase, stringResourceProvider)
        advanceUntilIdle()

        coVerify(exactly = 1) { useCase(forceRefresh = true) }
    }
}