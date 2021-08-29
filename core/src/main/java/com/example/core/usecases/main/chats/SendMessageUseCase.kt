package com.example.core.usecases.main.chats

import com.example.core.repositories.ChatsRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatsRepository: ChatsRepository
) {

    suspend fun invoke(messageContent: String, chatId: String, occupantsIds: List<Int>) =
        chatsRepository.sendMessage(messageContent, chatId, occupantsIds)
}