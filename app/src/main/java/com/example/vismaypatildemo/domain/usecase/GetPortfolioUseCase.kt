package com.example.vismaypatildemo.domain.usecase

import com.example.vismaypatildemo.domain.model.PortfolioSummary
import com.example.vismaypatildemo.domain.repository.PortfolioRepository
import javax.inject.Inject

class GetPortfolioUseCase @Inject constructor(
    private val repository: PortfolioRepository,
    private val calculatePortfolioSummaryUseCase: CalculatePortfolioSummaryUseCase
) {

    suspend operator fun invoke(forceRefresh: Boolean = false): Result<PortfolioSummary> {
        return try {
            val result = repository.getHoldings(forceRefresh)
            result.map { holdings -> calculatePortfolioSummaryUseCase(holdings) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun refresh(): Result<Unit> {
        return repository.refreshHoldings()
    }
}