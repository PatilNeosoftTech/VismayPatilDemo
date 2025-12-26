package com.example.vismaypatildemo.di


import com.example.vismaypatildemo.data.repository.PortfolioRepositoryImpl
import com.example.vismaypatildemo.domain.repository.PortfolioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPortfolioRepository(
        repositoryImpl: PortfolioRepositoryImpl
    ): PortfolioRepository
}