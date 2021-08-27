package com.example.chatapplication.ui.main.chats.mvi

sealed class ChatsStateEvent {

    object LoadAllChats : ChatsStateEvent()
    object FindUser: ChatsStateEvent()
    data class StartNewChat(val userId: Int): ChatsStateEvent()
}