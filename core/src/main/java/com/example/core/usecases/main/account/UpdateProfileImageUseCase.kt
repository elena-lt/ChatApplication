package com.example.core.usecases.main.account

import com.example.core.repositories.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class UpdateProfileImageUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {

    suspend fun invoke(image: File) = accountRepository.updateProfileImg(image).flowOn(Dispatchers.IO)
}