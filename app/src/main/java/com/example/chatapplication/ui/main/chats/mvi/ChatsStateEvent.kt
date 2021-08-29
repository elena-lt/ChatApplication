package com.example.chatapplication.ui.main.chats.mvi

import com.example.chatapplication.models.Models

sealed class ChatsStateEvent {

    object LoadAllChats : ChatsStateEvent()

    object FindUser : ChatsStateEvent()

    data class StartNewChat(val userId: Int) : ChatsStateEvent()

    data class RetrieveChatHistory(val chatId: String) : ChatsStateEvent()

    data class SendMessage(
        val messageContent: String,
        val chatId: String,
        val occupantsIds: List<Int>
    ) : ChatsStateEvent()

}