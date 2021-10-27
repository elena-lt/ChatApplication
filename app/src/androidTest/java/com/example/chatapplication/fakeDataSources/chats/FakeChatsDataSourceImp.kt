package com.example.chatapplication.fakeDataSources.chats

import com.example.chatapplication.models.Models
import com.example.core.models.ChatDialogDomain
import com.example.core.models.ChatMessageDomain
import com.example.core.models.UserDomain
import com.example.core.utils.DataState
import com.example.data.repositories.main.chats.ChatsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeChatsDataSourceImp : ChatsDataSource {
    private val chats = mutableListOf<ChatDialogDomain>()

    init {
        chats.add(ChatDialogDomain("1", "first message"))
        chats.add(ChatDialogDomain("1", "second message"))
        chats.add(ChatDialogDomain("1", "third message"))
    }

    override suspend fun loadAllChats(): Flow<DataState<MutableList<ChatDialogDomain>>> {
        return flowOf(DataState.SUCCESS(data = chats))
    }

    override suspend fun findUser(): Flow<DataState<MutableList<UserDomain>>> {
        TODO("Not yet implemented")
    }

    override suspend fun startNewChat(userId: Int): Flow<DataState<ChatDialogDomain>> {
        TODO("Not yet implemented")
    }

    override suspend fun retrieveChatHistory(chatId: String): Flow<DataState<MutableList<ChatMessageDomain>>> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(
        messageContent: String,
        chatId: String,
        occupantsIds: List<Int>
    ): Flow<DataState<String>> {
        TODO("Not yet implemented")
    }
}