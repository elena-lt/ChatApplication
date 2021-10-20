package com.example.data.repositories.authentication

import com.example.core.repositories.AuthenticationRepository
import java.io.File
import javax.inject.Inject

class AuthenticationRepositoryImp @Inject constructor(
    private val authDataSource: AuthenticationDataSource
) : AuthenticationRepository {

    override suspend fun loginUser(username: String, password: String) =
        authDataSource.loginUser(username, password)

    override suspend fun signUpUser(username: String, email: String, fullName: String, password: String, profileImage: File?)=
        authDataSource.signUpUser(username, email, fullName, password, profileImage)
}