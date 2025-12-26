package com.example.vismaypatildemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vismaypatildemo.data.local.dao.HoldingDao

@Database(
    entities = [HoldingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PortfolioDatabase : RoomDatabase() {

    abstract fun holdingDao(): HoldingDao

    companion object {
        const val DATABASE_NAME = "portfolio_database"
    }
}