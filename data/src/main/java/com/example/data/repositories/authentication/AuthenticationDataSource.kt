package com.example.data.repositories.authentication

import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AuthenticationDataSource {

    suspend fun signUpUser(
        username: String,
        email: String,
        password: String,
        profileImage: File?
    ): Flow<DataState<UserDomain>>

    suspend fun loginUser(login: String, password: String): Flow<DataState<UserDomain>>
}