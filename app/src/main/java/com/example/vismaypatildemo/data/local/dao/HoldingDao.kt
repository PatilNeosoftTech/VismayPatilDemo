package com.example.vismaypatildemo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vismaypatildemo.data.local.HoldingEntity

@Dao
interface HoldingDao {
    @Query("SELECT * FROM holdings ORDER BY symbol ASC")
    suspend fun getAllHoldings(): List<HoldingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoldings(holdings: List<HoldingEntity>)

    @Query("DELETE FROM holdings WHERE symbol IN (:symbols)")
    suspend fun deleteHoldings(symbols: List<String>)

    @Query("DELETE FROM holdings")
    suspend fun deleteAllHoldings()

    @Query("SELECT MAX(last_updated) FROM holdings")
    suspend fun getLastUpdateTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM holdings")
    suspend fun getHoldingsCount(): Int
}