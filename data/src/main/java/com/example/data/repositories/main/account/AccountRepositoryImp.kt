package com.example.data.repositories.main.account

import com.example.core.models.UserDomain
import com.example.core.repositories.AccountRepository
import com.example.core.utils.DataState
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class AccountRepositoryImp @Inject constructor(
    private val accountDataSource: AccountDataSource
) : AccountRepository {

    override suspend fun logout(): Flow<DataState<String>> = accountDataSource.logout()

    override suspend fun loadAccountProperties(): Flow<DataState<UserDomain>> =
        accountDataSource.loadAccountProperties()

    override suspend fun updateProfileImg(image: File): Flow<DataState<UserDomain>> =
        accountDataSource.updateProfileImage(image)
}