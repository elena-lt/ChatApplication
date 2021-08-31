package com.example.core.repositories

import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AccountRepository {

    suspend fun logout(): Flow<DataState<String>>
    suspend fun updateProfileImg(image: File): Flow<DataState<UserDomain>>
}