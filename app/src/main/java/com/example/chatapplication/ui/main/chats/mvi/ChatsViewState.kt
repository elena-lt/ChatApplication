package com.example.chatapplication.ui.main.chats.mvi

import com.example.chatapplication.models.ChatMessage
import com.example.chatapplication.models.Models
import com.quickblox.chat.model.QBChatDialog

data class ChatsViewState(
    val currUser: CurrLoggedInUser? = null,
    val chats: Chats? = null,
    val users: Users? = null,
    val openChatDialog: OpenChatDialog? = null
) {
    object Idle
    data class CurrLoggedInUser(val user: Models.User?)
    data class Chats(val chats: MutableList<Models.Chat>)
    data class Users(val users: MutableList<Models.User>)
    data class OpenChatDialog (val chatDialog: Models.Chat, val messages: MutableList<ChatMessage>?)
}