package com.example.vismaypatildemo.data.repository

import com.example.vismaypatildemo.R
import com.example.vismaypatildemo.data.local.dao.HoldingDao
import com.example.vismaypatildemo.data.mapper.toDomainFromEntity
import com.example.vismaypatildemo.data.mapper.toEntityList
import com.example.vismaypatildemo.core.NetworkConnectivityManager
import com.example.vismaypatildemo.data.remote.ApiService
import com.example.vismaypatildemo.domain.model.Holding
import com.example.vismaypatildemo.domain.repository.PortfolioRepository
import com.example.vismaypatildemo.core.StringResourceProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val holdingDao: HoldingDao,
    private val networkManager: NetworkConnectivityManager,
    private val stringResourceProvider: StringResourceProvider
) : PortfolioRepository {

    override suspend fun getHoldings(forceRefresh: Boolean): Result<List<Holding>> {
        return try {
            val hasLocalData = holdingDao.getHoldingsCount() > 0
            val isNetworkAvailable = networkManager.isNetworkAvailable()

            when {
                !hasLocalData && !isNetworkAvailable -> {
                    Result.failure(Exception(stringResourceProvider.getString(R.string.error_no_internet_connection)))
                }

                !isNetworkAvailable -> {
                    loadFromCache()
                }

                !hasLocalData || forceRefresh -> {
                    fetchFromNetworkAndCache()
                }

                else -> {
                    fetchFromNetworkAndCache().recoverCatching {
                        holdingDao.getAllHoldings().toDomainFromEntity()
                    }
                }
            }
        } catch (e: Exception) {
            val cachedData = holdingDao.getAllHoldings()
            if (cachedData.isNotEmpty()) {
                Result.success(cachedData.toDomainFromEntity())
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun refreshHoldings(): Result<Unit> {
        return try {
            if (!networkManager.isNetworkAvailable()) {
                return Result.failure(Exception(stringResourceProvider.getString(R.string.error_no_internet_connection_short)))
            }

            val response = apiService.getHoldings()
            val newHoldings = response.data.userHolding.toEntityList()
            val existingHoldings = holdingDao.getAllHoldings()

            val newSymbols = newHoldings.map { it.symbol }.toSet()
            val holdingsToDelete = existingHoldings.filter { it.symbol !in newSymbols }

            if (holdingsToDelete.isNotEmpty()) {
                holdingDao.deleteHoldings(holdingsToDelete.map { it.symbol })
            }

            holdingDao.insertHoldings(newHoldings)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCache() {
        holdingDao.deleteAllHoldings()
    }

    private suspend fun fetchFromNetworkAndCache(): Result<List<Holding>> {
        val response = apiService.getHoldings()
        val newHoldings = response.data.userHolding.toEntityList()
        val existingHoldings = holdingDao.getAllHoldings()

        val newSymbols = newHoldings.map { it.symbol }.toSet()
        val holdingsToDelete = existingHoldings.filter { it.symbol !in newSymbols }

        if (holdingsToDelete.isNotEmpty()) {
            holdingDao.deleteHoldings(holdingsToDelete.map { it.symbol })
        }

        holdingDao.insertHoldings(newHoldings)

        return Result.success(holdingDao.getAllHoldings().toDomainFromEntity())
    }

    private suspend fun loadFromCache(): Result<List<Holding>> {
        val cachedData = holdingDao.getAllHoldings()
        return if (cachedData.isNotEmpty()) {
            Result.success(cachedData.toDomainFromEntity())
        } else {
            Result.failure(Exception(stringResourceProvider.getString(R.string.error_no_data_available)))
        }
    }
}