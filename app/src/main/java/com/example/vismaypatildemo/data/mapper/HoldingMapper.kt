package com.example.vismaypatildemo.data.mapper

import com.example.vismaypatildemo.data.local.HoldingEntity
import com.example.vismaypatildemo.data.remote.dto.HoldingDto
import com.example.vismaypatildemo.domain.model.Holding

fun HoldingDto.toDomain(): Holding = Holding(
    symbol = symbol,
    quantity = quantity,
    ltp = ltp,
    avgPrice = avgPrice,
    close = close
)

fun HoldingDto.toEntity(): HoldingEntity = HoldingEntity(
    symbol = symbol,
    quantity = quantity,
    ltp = ltp,
    avgPrice = avgPrice,
    close = close,
    lastUpdated = System.currentTimeMillis()
)

fun HoldingEntity.toDomain(): Holding = Holding(
    symbol = symbol,
    quantity = quantity,
    ltp = ltp,
    avgPrice = avgPrice,
    close = close
)

fun List<HoldingDto>.toDomainFromDto(): List<Holding> = map { it.toDomain() }

fun List<HoldingDto>.toEntityList(): List<HoldingEntity> = map { it.toEntity() }

fun List<HoldingEntity>.toDomainFromEntity(): List<Holding> = map { it.toDomain() }