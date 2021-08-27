package com.example.data.repositories.authentication

import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import kotlinx.coroutines.flow.Flow

interface AuthenticationDataSource {

    fun signUpUser(username: String, password: String): Flow<DataState<UserDomain>>

    suspend fun loginUser(login: String, password: String): Flow<DataState<UserDomain>>
}