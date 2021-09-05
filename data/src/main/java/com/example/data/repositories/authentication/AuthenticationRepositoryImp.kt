package com.example.data.repositories.authentication

import com.example.core.repositories.AuthenticationRepository
import com.example.data.repositories.authentication.AuthenticationDataSource
import java.io.File
import javax.inject.Inject

class AuthenticationRepositoryImp @Inject constructor(
    private val authDataSource: AuthenticationDataSource
) : AuthenticationRepository {

    override suspend fun loginUser(login: String, password: String) =
        authDataSource.loginUser(login, password)

    override suspend fun signUpUser(username: String, email: String, password: String, profileImage: File?)=
        authDataSource.signUpUser(username, email, password, profileImage)
}