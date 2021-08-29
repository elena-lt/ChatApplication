package com.example.data.di.authentication

import com.example.core.repositories.AuthenticationRepository
import com.example.data.repositories.authentication.AuthenticationDataSource
import com.example.data.repositories.authentication.AuthenticationDataSourceImp
import com.example.data.repositories.authentication.AuthenticationRepositoryImp
import com.example.data.sessionManager.SessionManger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthenticationModule {

    @Provides
    @Singleton
    fun authenticationRepository(authDataSource: AuthenticationDataSource): AuthenticationRepository =
        AuthenticationRepositoryImp(authDataSource)

    @Provides
    @Singleton
    fun provideAutheticationDataSource(sessionManger: SessionManger): AuthenticationDataSource =
        AuthenticationDataSourceImp(sessionManger)
}