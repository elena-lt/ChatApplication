package com.example.chatapplication.ui.main.chats.mvi

import com.example.chatapplication.models.Models
import com.quickblox.chat.model.QBChatDialog

sealed class ChatsViewState {

    object Idle : ChatsViewState()
    data class Chats(val chats: MutableList<QBChatDialog>): ChatsViewState()
    data class Users(val users: MutableList<Models.User>): ChatsViewState()
}