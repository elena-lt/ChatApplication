package com.example.data.repositories.authentication

import com.example.core.repositories.AuthenticationRepository
import com.example.data.repositories.authentication.AuthenticationDataSource
import javax.inject.Inject

class AuthenticationRepositoryImp @Inject constructor(
    private val authDataSource: AuthenticationDataSource
) : AuthenticationRepository {

    override suspend fun loginUser(login: String, password: String) =
        authDataSource.loginUser(login, password)

    override fun signUpUser(username: String, password: String)=
        authDataSource.signUpUser(username, password)
}