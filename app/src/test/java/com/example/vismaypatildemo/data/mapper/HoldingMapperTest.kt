package com.example.vismaypatildemo.data.mapper

import com.example.vismaypatildemo.data.local.HoldingEntity
import com.example.vismaypatildemo.data.remote.dto.HoldingDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HoldingMapperTest {

    @Test
    fun `toDomain maps HoldingDto to Holding correctly`() {
        val dto = HoldingDto(
            symbol = "TCS",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 105.0
        )

        val domain = dto.toDomain()

        assertThat(domain.symbol).isEqualTo("TCS")
        assertThat(domain.quantity).isEqualTo(10)
        assertThat(domain.ltp).isEqualTo(100.0)
        assertThat(domain.avgPrice).isEqualTo(90.0)
        assertThat(domain.close).isEqualTo(105.0)
    }

    @Test
    fun `toEntity maps HoldingDto to HoldingEntity correctly`() {
        val dto = HoldingDto(
            symbol = "TCS",
            quantity = 10,
            ltp = 100.0,
            avgPrice = 90.0,
            close = 105.0
        )

        val entity = dto.toEntity()

        assertThat(entity.symbol).isEqualTo("TCS")
        assertThat(entity.quantity).isEqualTo(10)
        assertThat(entity.ltp).isEqualTo(100.0)
        assertThat(entity.avgPrice).isEqualTo(90.0)
        assertThat(entity.close).isEqualTo(105.0)
        assertThat(entity.lastUpdated).isGreaterThan(0L)
    }

    @Test
    fun `toDomain maps HoldingEntity to Holding correctly`() {
        val entity = HoldingEntity(
            symbol = "INFY",
            quantity = 5,
            ltp = 200.0,
            avgPrice = 180.0,
            close = 210.0,
            lastUpdated = System.currentTimeMillis()
        )

        val domain = entity.toDomain()

        assertThat(domain.symbol).isEqualTo("INFY")
        assertThat(domain.quantity).isEqualTo(5)
        assertThat(domain.ltp).isEqualTo(200.0)
        assertThat(domain.avgPrice).isEqualTo(180.0)
        assertThat(domain.close).isEqualTo(210.0)
    }

    @Test
    fun `toDomainFromDto maps list of HoldingDto to list of Holding`() {
        val dtoList = listOf(
            HoldingDto("TCS", 10, 100.0, 90.0, 105.0),
            HoldingDto("INFY", 5, 200.0, 180.0, 210.0)
        )

        val domainList = dtoList.toDomainFromDto()

        assertThat(domainList.size).isEqualTo(2)
        assertThat(domainList[0].symbol).isEqualTo("TCS")
        assertThat(domainList[1].symbol).isEqualTo("INFY")
    }

    @Test
    fun `toEntityList maps list of HoldingDto to list of HoldingEntity`() {
        val dtoList = listOf(
            HoldingDto("TCS", 10, 100.0, 90.0, 105.0),
            HoldingDto("INFY", 5, 200.0, 180.0, 210.0)
        )

        val entityList = dtoList.toEntityList()

        assertThat(entityList.size).isEqualTo(2)
        assertThat(entityList[0].symbol).isEqualTo("TCS")
        assertThat(entityList[1].symbol).isEqualTo("INFY")
        assertThat(entityList[0].lastUpdated).isGreaterThan(0L)
        assertThat(entityList[1].lastUpdated).isGreaterThan(0L)
    }

    @Test
    fun `toDomainFromEntity maps list of HoldingEntity to list of Holding`() {
        val entityList = listOf(
            HoldingEntity("TCS", 10, 100.0, 90.0, 105.0, System.currentTimeMillis()),
            HoldingEntity("INFY", 5, 200.0, 180.0, 210.0, System.currentTimeMillis())
        )

        val domainList = entityList.toDomainFromEntity()

        assertThat(domainList.size).isEqualTo(2)
        assertThat(domainList[0].symbol).isEqualTo("TCS")
        assertThat(domainList[1].symbol).isEqualTo("INFY")
    }

    @Test
    fun `toDomainFromDto returns empty list for empty input`() {
        val emptyList = emptyList<HoldingDto>()

        val result = emptyList.toDomainFromDto()

        assertThat(result).isEmpty()
    }

    @Test
    fun `toEntityList returns empty list for empty input`() {
        val emptyList = emptyList<HoldingDto>()

        val result = emptyList.toEntityList()

        assertThat(result).isEmpty()
    }

    @Test
    fun `toDomainFromEntity returns empty list for empty input`() {
        val emptyList = emptyList<HoldingEntity>()

        val result = emptyList.toDomainFromEntity()

        assertThat(result).isEmpty()
    }
}