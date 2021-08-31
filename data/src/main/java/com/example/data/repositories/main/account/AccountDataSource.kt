package com.example.data.repositories.main.account

import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AccountDataSource {

    suspend fun logout(): Flow<DataState<String>>
    suspend fun updateProfileImage(image: File): Flow<DataState<UserDomain>>
}