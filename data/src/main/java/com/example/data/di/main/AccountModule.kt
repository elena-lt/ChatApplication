package com.example.data.di.main

import android.content.SharedPreferences
import com.example.core.repositories.AccountRepository
import com.example.data.persistance.AccountPropertiesDao
import com.example.data.persistance.AppDatabase
import com.example.data.repositories.main.account.AccountDataSource
import com.example.data.repositories.main.account.AccountDataSourceImp
import com.example.data.repositories.main.account.AccountRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AccountModule {

    @Provides
    @Singleton
    fun provideAccountPropertiesDao(database: AppDatabase): AccountPropertiesDao =
        database.getAccountPropertiesDao()

    @Provides
    @Singleton
    fun provideAccountDataSource(sharedPreferences: SharedPreferences): AccountDataSource =
        AccountDataSourceImp(sharedPreferences)

    @Provides
    @Singleton
    fun provideAccountRepository(dataSource: AccountDataSource): AccountRepository =
        AccountRepositoryImp(dataSource)

}