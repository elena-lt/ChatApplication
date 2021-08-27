package com.example.core.usecases.main.chats

import com.example.core.repositories.ChatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FindUserUseCase @Inject constructor(
    private val chatsRepository: ChatsRepository
) {

    suspend fun invoke() = chatsRepository.findUser().flowOn(Dispatchers.IO)
}