package com.example.chatapplication.fakeDataSources.account

import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import com.example.data.repositories.main.account.AccountDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.File

class FakeAccountDataSource : AccountDataSource {

    override suspend fun logout(): Flow<DataState<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun loadAccountProperties(): Flow<DataState<UserDomain>> {
        return flowOf(
            DataState.SUCCESS(
                UserDomain(
                    1,
                    "user1",
                    "Test User",
                    "user1@test.com",
                    null,
                    null
                )
            )
        )
    }

    override suspend fun updateProfileImage(image: File): Flow<DataState<UserDomain>> {
        TODO("Not yet implemented")
    }
}