package com.example.core.usecases.main.chats

import com.example.core.repositories.ChatsRepository
import javax.inject.Inject

class StartNewChatUseCase @Inject constructor(
    private val chatsRepository: ChatsRepository
){
    suspend fun invoke (userId: Int) = chatsRepository.startNewChat(userId)
}