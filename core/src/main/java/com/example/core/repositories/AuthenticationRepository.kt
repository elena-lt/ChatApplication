package com.example.core.repositories

import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {

    suspend fun loginUser(username: String, password: String): Flow<DataState<UserDomain>>
    fun signUpUser(username: String, password: String):  Flow<DataState<UserDomain>>
}