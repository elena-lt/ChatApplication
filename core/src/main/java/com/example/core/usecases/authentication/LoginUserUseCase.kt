package com.example.core.usecases.authentication

import com.example.core.repositories.AuthenticationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthenticationRepository
) {

    suspend fun execute(username: String, password: String) =
        authRepository.loginUser(username, password).flowOn(Dispatchers.IO)
}