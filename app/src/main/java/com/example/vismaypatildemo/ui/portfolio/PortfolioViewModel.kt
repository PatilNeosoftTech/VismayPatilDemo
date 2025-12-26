package com.example.vismaypatildemo.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vismaypatildemo.R
import com.example.vismaypatildemo.domain.usecase.GetPortfolioUseCase
import com.example.vismaypatildemo.core.StringResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val getPortfolioUseCase: GetPortfolioUseCase,
    private val stringResourceProvider: StringResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

    init {
        loadPortfolio()
    }

    private fun loadPortfolio() {
        viewModelScope.launch {
            _uiState.value = PortfolioUiState.Loading

            getPortfolioUseCase(forceRefresh = true)
                .onSuccess { portfolio ->
                    _uiState.value = PortfolioUiState.Success(
                        portfolio = portfolio,
                        isSummaryExpanded = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = PortfolioUiState.Error(
                        message = error.message ?: stringResourceProvider.getString(R.string.error_failed_to_load_portfolio)
                    )
                }
        }
    }

    fun refreshPortfolio() {
        viewModelScope.launch {
            getPortfolioUseCase.refresh()
                .onSuccess {
                    loadPortfolio()
                }
                .onFailure { error ->
                    val currentState = _uiState.value
                    if (currentState !is PortfolioUiState.Success) {
                        _uiState.value = PortfolioUiState.Error(
                            message = error.message ?: stringResourceProvider.getString(R.string.error_failed_to_refresh)
                        )
                    }
                }
        }
    }

    fun toggleSummaryExpansion() {
        val currentState = _uiState.value
        if (currentState is PortfolioUiState.Success) {
            _uiState.value = currentState.copy(
                isSummaryExpanded = !currentState.isSummaryExpanded
            )
        }
    }

    fun retry() {
        loadPortfolio()
    }
}