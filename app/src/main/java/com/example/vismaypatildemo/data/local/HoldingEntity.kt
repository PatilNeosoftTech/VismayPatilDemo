package com.example.vismaypatildemo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey
    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @ColumnInfo(name = "ltp")
    val ltp: Double,

    @ColumnInfo(name = "avg_price")
    val avgPrice: Double,

    @ColumnInfo(name = "close")
    val close: Double,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)