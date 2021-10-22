package com.example.chatapplication.ui.main.chats.mvi

sealed class ChatsStateEvent {

    object LoadCurrUserAccount: ChatsStateEvent()

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