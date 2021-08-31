package com.example.core.usecases.main.account

import com.example.core.repositories.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val accountRepository: AccountRepository
){

    suspend fun invoke() = accountRepository.logout().flowOn(Dispatchers.IO)
}