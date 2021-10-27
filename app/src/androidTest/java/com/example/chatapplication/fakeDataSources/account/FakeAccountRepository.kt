package com.example.chatapplication.fakeDataSources.account

import com.example.core.models.UserDomain
import com.example.core.repositories.AccountRepository
import com.example.core.utils.DataState
import com.example.data.repositories.main.account.AccountDataSource
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class FakeAccountRepository @Inject constructor(private val dataSource: AccountDataSource): AccountRepository {
    override suspend fun logout(): Flow<DataState<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun loadAccountProperties(): Flow<DataState<UserDomain>> {
        return dataSource.loadAccountProperties()
    }

    override suspend fun updateProfileImg(image: File): Flow<DataState<UserDomain>> {
        TODO("Not yet implemented")
    }
}