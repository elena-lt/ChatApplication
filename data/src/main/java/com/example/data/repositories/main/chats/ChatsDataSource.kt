package com.example.data.repositories.main.chats

import com.example.core.models.ChatDialogDomain
import com.example.core.models.ChatMessageDomain
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import kotlinx.coroutines.flow.Flow

interface ChatsDataSource {

    suspend fun loadAllChats(): Flow<DataState<MutableList<ChatDialogDomain>>>

    suspend fun loadAllUsers(): Flow<DataState<MutableList<UserDomain>>>

    suspend fun startNewChat(userId: Int): Flow<DataState<ChatDialogDomain>>

    suspend fun retrieveChatHistory(chatId: String): Flow<DataState<MutableList<ChatMessageDomain>>>

    suspend fun sendMessage(
        messageContent: String,
        chatId: String,
        occupantsIds: List<Int>
    ): Flow<DataState<String>>
}