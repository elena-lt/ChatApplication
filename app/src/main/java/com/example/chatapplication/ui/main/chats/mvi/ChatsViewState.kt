package com.example.chatapplication.ui.main.chats.mvi

import com.example.chatapplication.models.Models
import com.quickblox.chat.model.QBChatDialog

data class ChatsViewState(
    val chats: Chats? = null,
    val users: Users? = null
) {
    object Idle
    data class Chats(val chats: MutableList<Models.Chat>)
    data class Users(val users: MutableList<Models.User>)
}