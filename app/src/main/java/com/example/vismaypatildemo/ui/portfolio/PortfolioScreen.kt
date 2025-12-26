package com.example.vismaypatildemo.ui.portfolio

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vismaypatildemo.domain.model.Holding
import com.example.vismaypatildemo.domain.model.PortfolioSummary
import com.example.vismaypatildemo.domain.usecase.CalculatePortfolioSummaryUseCase
import com.example.vismaypatildemo.ui.portfolio.components.PortfolioErrorContent
import com.example.vismaypatildemo.ui.portfolio.components.PortfolioLoadingContent
import com.example.vismaypatildemo.ui.portfolio.components.PortfolioSuccessContent
import com.example.vismaypatildemo.ui.portfolio.components.PortfolioTopAppBar

@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PortfolioScreenContent(
        uiState = uiState,
        onToggleSummary = viewModel::toggleSummaryExpansion,
        onRetry = viewModel::retry
    )
}

@Composable
private fun PortfolioScreenContent(
    uiState: PortfolioUiState,
    onToggleSummary: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { PortfolioTopAppBar() },
        modifier = modifier
    ) { paddingValues ->
        when (uiState) {
            is PortfolioUiState.Loading -> {
                PortfolioLoadingContent(
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is PortfolioUiState.Success -> {
                PortfolioSuccessContent(
                    portfolio = uiState.portfolio,
                    isSummaryExpanded = uiState.isSummaryExpanded,
                    onToggleSummary = onToggleSummary,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is PortfolioUiState.Error -> {
                PortfolioErrorContent(
                    message = uiState.message,
                    onRetry = onRetry,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PortfolioScreenLoadingPreview() {
    PortfolioScreenContent(
        uiState = PortfolioUiState.Loading,
        onToggleSummary = {},
        onRetry = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun PortfolioScreenErrorPreview() {
    PortfolioScreenContent(
        uiState = PortfolioUiState.Error("No internet connection. Please check your connection and try again."),
        onToggleSummary = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun PortfolioScreenSuccessPreview() {
    val sampleHoldings = listOf(
        Holding("AAPL", 10, 150.0, 140.0, 148.0),
        Holding("GOOGL", 5, 2800.0, 2700.0, 2790.0),
        Holding("TSLA", 8, 800.0, 750.0, 795.0)
    )

    PortfolioScreenContent(
        uiState = PortfolioUiState.Success(
            portfolio = CalculatePortfolioSummaryUseCase()(sampleHoldings),
            isSummaryExpanded = false
        ),
        onToggleSummary = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun PortfolioScreenSuccessExpandedPreview() {
    val sampleHoldings = listOf(
        Holding("AAPL", 10, 150.0, 140.0, 148.0),
        Holding("GOOGL", 5, 2800.0, 2700.0, 2790.0)
    )

    PortfolioScreenContent(
        uiState = PortfolioUiState.Success(
            portfolio = CalculatePortfolioSummaryUseCase()(sampleHoldings),
            isSummaryExpanded = true
        ),
        onToggleSummary = {},
        onRetry = {}
    )
}