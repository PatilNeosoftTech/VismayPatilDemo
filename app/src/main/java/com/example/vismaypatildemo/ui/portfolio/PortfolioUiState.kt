package com.example.vismaypatildemo.ui.portfolio

import com.example.vismaypatildemo.domain.model.PortfolioSummary

sealed interface PortfolioUiState {

    data object Loading : PortfolioUiState

    data class Success(
        val portfolio: PortfolioSummary,
        val isSummaryExpanded: Boolean = false
    ) : PortfolioUiState

    data class Error(
        val message: String
    ) : PortfolioUiState
}