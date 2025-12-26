package com.example.vismaypatildemo.domain.repository

import com.example.vismaypatildemo.domain.model.Holding

interface PortfolioRepository {
    suspend fun getHoldings(forceRefresh: Boolean = false): Result<List<Holding>>
    suspend fun refreshHoldings(): Result<Unit>
    suspend fun clearCache()
}