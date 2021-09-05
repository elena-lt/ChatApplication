package com.example.core.repositories

import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AuthenticationRepository {

    suspend fun loginUser(username: String, password: String): Flow<DataState<UserDomain>>
    suspend fun signUpUser(
        username: String,
        email: String,
        password: String,
        profileImage: File?
    ): Flow<DataState<UserDomain>>
}