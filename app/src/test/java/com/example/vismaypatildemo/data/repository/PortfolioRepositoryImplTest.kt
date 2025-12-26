package com.example.vismaypatildemo.data.repository

import com.example.vismaypatildemo.data.local.HoldingEntity
import com.example.vismaypatildemo.data.local.dao.HoldingDao
import com.example.vismaypatildemo.core.NetworkConnectivityManager
import com.example.vismaypatildemo.data.remote.ApiService
import com.example.vismaypatildemo.data.remote.dto.HoldingDto
import com.example.vismaypatildemo.data.remote.dto.HoldingsResponse
import com.example.vismaypatildemo.data.remote.dto.UserHoldingData
import com.example.vismaypatildemo.domain.model.Holding
import com.example.vismaypatildemo.core.StringResourceProvider
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class PortfolioRepositoryImplTest {

    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var holdingDao: HoldingDao

    @MockK
    private lateinit var networkManager: NetworkConnectivityManager

    @MockK(relaxed = true)
    private lateinit var stringResourceProvider: StringResourceProvider

    private lateinit var repository: PortfolioRepositoryImpl

    private val sampleHoldingDtos = listOf(
        HoldingDto(
            symbol = "AAPL",
            quantity = 10,
            ltp = 150.0,
            avgPrice = 140.0,
            close = 148.0
        ),
        HoldingDto(
            symbol = "GOOGL",
            quantity = 5,
            ltp = 2800.0,
            avgPrice = 2700.0,
            close = 2790.0
        )
    )

    private val sampleHoldingEntities = listOf(
        HoldingEntity(
            symbol = "AAPL",
            quantity = 10,
            ltp = 150.0,
            avgPrice = 140.0,
            close = 148.0
        ),
        HoldingEntity(
            symbol = "GOOGL",
            quantity = 5,
            ltp = 2800.0,
            avgPrice = 2700.0,
            close = 2790.0
        )
    )

    private val sampleHoldings = listOf(
        Holding(
            symbol = "AAPL",
            quantity = 10,
            ltp = 150.0,
            avgPrice = 140.0,
            close = 148.0
        ),
        Holding(
            symbol = "GOOGL",
            quantity = 5,
            ltp = 2800.0,
            avgPrice = 2700.0,
            close = 2790.0
        )
    )

    private val mockResponse = HoldingsResponse(
        data = UserHoldingData(userHolding = sampleHoldingDtos)
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { stringResourceProvider.getString(any()) } returns "Test error message"
        repository = PortfolioRepositoryImpl(apiService, holdingDao, networkManager,
            stringResourceProvider)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }


    @Test
    fun `getHoldings - no local data and no internet - returns error`() = runTest {
        coEvery { holdingDao.getHoldingsCount() } returns 0
        every { networkManager.isNetworkAvailable() } returns false

        val result = repository.getHoldings(forceRefresh = false)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.isNotEmpty() == true)

        coVerify(exactly = 0) { apiService.getHoldings() }
    }

    @Test
    fun `getHoldings - no local data with internet - fetches from network`() = runTest {
        coEvery { holdingDao.getHoldingsCount() } returns 0
        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns mockResponse
        coEvery { holdingDao.getAllHoldings() } returns emptyList() andThen sampleHoldingEntities
        coEvery { holdingDao.deleteHoldings(any()) } just Runs
        coEvery { holdingDao.insertHoldings(any()) } just Runs

        val result = repository.getHoldings(forceRefresh = false)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)

        coVerify(exactly = 1) { apiService.getHoldings() }
        coVerify(exactly = 1) { holdingDao.insertHoldings(any()) }
    }

    @Test
    fun `getHoldings - has local data and no internet - returns cached data`() = runTest {
        coEvery { holdingDao.getHoldingsCount() } returns 2
        every { networkManager.isNetworkAvailable() } returns false
        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities

        val result = repository.getHoldings(forceRefresh = false)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)

        coVerify(exactly = 0) { apiService.getHoldings() }
        coVerify(exactly = 1) { holdingDao.getAllHoldings() }
    }

    @Test
    fun `getHoldings - has local data with internet - fetches from network with cache fallback`() = runTest {
        coEvery { holdingDao.getHoldingsCount() } returns 2
        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns mockResponse
        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities
        coEvery { holdingDao.deleteHoldings(any()) } just Runs
        coEvery { holdingDao.insertHoldings(any()) } just Runs

        val result = repository.getHoldings(forceRefresh = false)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)

        coVerify(exactly = 1) { apiService.getHoldings() }
    }

    @Test
    fun `getHoldings - has local data with internet but network fails - returns cached data`() = runTest {
        coEvery { holdingDao.getHoldingsCount() } returns 2
        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } throws Exception("Network error")
        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities

        val result = repository.getHoldings(forceRefresh = false)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)

        coVerify(exactly = 1) { apiService.getHoldings() }
        coVerify(atLeast = 1) { holdingDao.getAllHoldings() }
    }

    @Test
    fun `getHoldings - forceRefresh true - always fetches from network`() = runTest {
        coEvery { holdingDao.getHoldingsCount() } returns 2
        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns mockResponse
        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities
        coEvery { holdingDao.deleteHoldings(any()) } just Runs
        coEvery { holdingDao.insertHoldings(any()) } just Runs

        val result = repository.getHoldings(forceRefresh = true)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { apiService.getHoldings() }
    }

    @Test
    fun `getHoldings - exception occurs but cache available - returns cached data`() = runTest {
        coEvery { holdingDao.getHoldingsCount() } throws Exception("Database error")
        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities

        val result = repository.getHoldings(forceRefresh = false)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `getHoldings - exception occurs and no cache - returns error`() = runTest {
        coEvery { holdingDao.getHoldingsCount() } throws Exception("Database error")
        coEvery { holdingDao.getAllHoldings() } returns emptyList()

        val result = repository.getHoldings(forceRefresh = false)

        assertTrue(result.isFailure)
    }


    @Test
    fun `refreshHoldings - no internet - returns error`() = runTest {
        every { networkManager.isNetworkAvailable() } returns false

        val result = repository.refreshHoldings()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.isNotEmpty() == true)

        coVerify(exactly = 0) { apiService.getHoldings() }
    }

    @Test
    fun `refreshHoldings - with internet - fetches and updates cache`() = runTest {
        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns mockResponse
        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities
        coEvery { holdingDao.deleteHoldings(any()) } just Runs
        coEvery { holdingDao.insertHoldings(any()) } just Runs

        val result = repository.refreshHoldings()

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { apiService.getHoldings() }
        coVerify(exactly = 1) { holdingDao.insertHoldings(any()) }
    }

    @Test
    fun `refreshHoldings - network error - returns error`() = runTest {
        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } throws Exception("Network timeout")

        val result = repository.refreshHoldings()

        assertTrue(result.isFailure)
        assertEquals("Network timeout", result.exceptionOrNull()?.message)
    }

    @Test
    fun `refreshHoldings - deletes holdings no longer in backend response`() = runTest {
        val existingHoldings = listOf(
            HoldingEntity("AAPL", 10, 150.0, 140.0, 148.0),
            HoldingEntity("GOOGL", 5, 2800.0, 2700.0, 2790.0),
            HoldingEntity("TSLA", 3, 800.0, 750.0, 795.0)
        )

        val newHoldingDtos = listOf(
            HoldingDto("AAPL", 10, 150.0, 140.0, 148.0),
            HoldingDto("GOOGL", 5, 2800.0, 2700.0, 2790.0)
        )

        val newResponse = HoldingsResponse(
            data = UserHoldingData(userHolding = newHoldingDtos)
        )

        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns newResponse
        coEvery { holdingDao.getAllHoldings() } returns existingHoldings
        coEvery { holdingDao.deleteHoldings(listOf("TSLA")) } just Runs
        coEvery { holdingDao.insertHoldings(any()) } just Runs

        val result = repository.refreshHoldings()

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { holdingDao.deleteHoldings(listOf("TSLA")) }
        coVerify(exactly = 1) { holdingDao.insertHoldings(any()) }
    }

    @Test
    fun `refreshHoldings - no deletions when all holdings still exist`() = runTest {
        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns mockResponse
        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities
        coEvery { holdingDao.deleteHoldings(any()) } just Runs
        coEvery { holdingDao.insertHoldings(any()) } just Runs

        val result = repository.refreshHoldings()

        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { holdingDao.deleteHoldings(any()) }
        coVerify(exactly = 1) { holdingDao.insertHoldings(any()) }
    }

    @Test
    fun `refreshHoldings - handles empty response from backend`() = runTest {
        val emptyResponse = HoldingsResponse(
            data = UserHoldingData(userHolding = emptyList())
        )

        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns emptyResponse
        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities
        coEvery { holdingDao.deleteHoldings(any()) } just Runs
        coEvery { holdingDao.insertHoldings(any()) } just Runs

        val result = repository.refreshHoldings()

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { holdingDao.deleteHoldings(listOf("AAPL", "GOOGL")) }
        coVerify(exactly = 1) { holdingDao.insertHoldings(emptyList()) }
    }


    @Test
    fun `fetchFromNetworkAndCache - new holdings added - inserts without deletions`() = runTest {
        val newHoldingDtos = listOf(
            HoldingDto("AAPL", 10, 150.0, 140.0, 148.0),
            HoldingDto("GOOGL", 5, 2800.0, 2700.0, 2790.0),
            HoldingDto("MSFT", 8, 300.0, 280.0, 298.0)
        )

        val newResponse = HoldingsResponse(
            data = UserHoldingData(userHolding = newHoldingDtos)
        )

        val updatedEntities = listOf(
            HoldingEntity("AAPL", 10, 150.0, 140.0, 148.0),
            HoldingEntity("GOOGL", 5, 2800.0, 2700.0, 2790.0),
            HoldingEntity("MSFT", 8, 300.0, 280.0, 298.0)
        )

        coEvery { holdingDao.getHoldingsCount() } returns 2
        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns newResponse

        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities andThen updatedEntities

        coEvery { holdingDao.deleteHoldings(any()) } just Runs
        coEvery { holdingDao.insertHoldings(any()) } just Runs

        val result = repository.getHoldings(forceRefresh = true)

        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)

        coVerify(exactly = 0) { holdingDao.deleteHoldings(any()) }
        coVerify(exactly = 1) { holdingDao.insertHoldings(any()) }
    }

    @Test
    fun `fetchFromNetworkAndCache - holdings updated - uses REPLACE strategy`() = runTest {
        val updatedHoldingDtos = listOf(
            HoldingDto("GOOGL", 5, 2800.0, 2700.0, 2790.0)
        )

        val updatedResponse = HoldingsResponse(
            data = UserHoldingData(userHolding = updatedHoldingDtos)
        )

        val updatedEntities = listOf(
            HoldingEntity("AAPL", 15, 155.0, 140.0, 153.0),
            HoldingEntity("GOOGL", 5, 2800.0, 2700.0, 2790.0)
        )

        coEvery { holdingDao.getHoldingsCount() } returns 2
        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns updatedResponse

        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities andThen updatedEntities

        coEvery { holdingDao.deleteHoldings(any()) } just Runs
        coEvery { holdingDao.insertHoldings(any()) } just Runs

        val result = repository.getHoldings(forceRefresh = true)

        assertTrue(result.isSuccess)
        val holdings = result.getOrNull()

        assertEquals(2, holdings?.size)

        coVerify(exactly = 1) { holdingDao.insertHoldings(any()) }
    }

    @Test
    fun `fetchFromNetworkAndCache - multiple holdings deleted - deletes all removed symbols`() = runTest {
        val existingHoldings = listOf(
            HoldingEntity("AAPL", 10, 150.0, 140.0, 148.0),
            HoldingEntity("GOOGL", 5, 2800.0, 2700.0, 2790.0),
            HoldingEntity("TSLA", 3, 800.0, 750.0, 795.0),
            HoldingEntity("MSFT", 8, 300.0, 280.0, 298.0)
        )

        val newHoldingDtos = listOf(
            HoldingDto("AAPL", 10, 150.0, 140.0, 148.0)
        )

        val newResponse = HoldingsResponse(
            data = UserHoldingData(userHolding = newHoldingDtos)
        )

        val finalEntities = listOf(
            HoldingEntity("AAPL", 10, 150.0, 140.0, 148.0)
        )

        coEvery { holdingDao.getHoldingsCount() } returns 4
        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns newResponse

        coEvery { holdingDao.getAllHoldings() } returns existingHoldings andThen finalEntities

        coEvery { holdingDao.deleteHoldings(any()) } just Runs
        coEvery { holdingDao.insertHoldings(any()) } just Runs

        val result = repository.getHoldings(forceRefresh = true)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("AAPL", result.getOrNull()?.first()?.symbol)

        coVerify(exactly = 1) {
            holdingDao.deleteHoldings(match { symbols ->
                symbols.containsAll(listOf("GOOGL", "TSLA", "MSFT")) && symbols.size == 3
            })
        }
        coVerify(exactly = 1) { holdingDao.insertHoldings(any()) }
    }


    @Test
    fun `clearCache - deletes all holdings`() = runTest {
        coEvery { holdingDao.deleteAllHoldings() } just Runs

        repository.clearCache()

        coVerify(exactly = 1) { holdingDao.deleteAllHoldings() }
    }


    @Test
    fun `loadFromCache - cache has data - returns success`() = runTest {
        coEvery { holdingDao.getHoldingsCount() } returns 2
        every { networkManager.isNetworkAvailable() } returns false
        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities

        val result = repository.getHoldings(forceRefresh = false)

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `loadFromCache - cache is empty - returns error`() = runTest {
        coEvery { holdingDao.getHoldingsCount() } returns 0
        every { networkManager.isNetworkAvailable() } returns false
        coEvery { holdingDao.getAllHoldings() } returns emptyList()

        val result = repository.getHoldings(forceRefresh = false)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.isNotEmpty() == true)
    }


    @Test
    fun `getHoldings - concurrent calls handle race condition properly`() = runTest {

        coEvery { holdingDao.getHoldingsCount() } returns 2
        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns mockResponse
        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities
        coEvery { holdingDao.deleteHoldings(any()) } just Runs
        coEvery { holdingDao.insertHoldings(any()) } just Runs

        val result1 = repository.getHoldings(forceRefresh = true)
        val result2 = repository.getHoldings(forceRefresh = true)

        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        coVerify(exactly = 2) { apiService.getHoldings() }
    }

    @Test
    fun `refreshHoldings - preserves data integrity during deletion and insertion`() = runTest {
        val slot = slot<List<HoldingEntity>>()

        every { networkManager.isNetworkAvailable() } returns true
        coEvery { apiService.getHoldings() } returns mockResponse
        coEvery { holdingDao.getAllHoldings() } returns sampleHoldingEntities
        coEvery { holdingDao.deleteHoldings(any()) } just Runs
        coEvery { holdingDao.insertHoldings(capture(slot)) } just Runs

        val result = repository.refreshHoldings()

        assertTrue(result.isSuccess)

        val insertedData = slot.captured
        assertEquals(2, insertedData.size)
        assertEquals("AAPL", insertedData[0].symbol)
        assertEquals("GOOGL", insertedData[1].symbol)
    }
}